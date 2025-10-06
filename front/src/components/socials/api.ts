import { useAuth } from "react-oidc-context";

const GATEWAY_BASE = "http://localhost:8080/socials";

export type UUID = string;

export interface ProfileDTO {
  id?: UUID;
  tag?: string;
  displayName?: string;
  currentOccupation?: string;
  profilePictureUrl?: string;
  bannerPictureUrl?: string;
  status?: string;
  employeeRating?: number;
  bioMarkdown?: string;
  followersAmount?: number;
  followingAmount?: number;
}

export interface PostDTO {
  id: UUID;
  reposted: boolean;
  profile: { tag?: string; displayName?: string; profilePictureUrl?: string };
  originalPost?: PostDTO | null;
  textContent?: string;
  createdAt?: string;
  likesAmount?: number;
}

export interface PostCreationDTO {
  reposted: boolean;
  originalPostId?: UUID | null;
  textContent?: string;
}

export interface LikeCreationDTO { postId: UUID }

export interface CommentDTO {
  id: UUID;
  contentEntityId: UUID;
  profile?: ProfileDTO;
  content: string;
  isAReply?: boolean;
  originalCommentId?: UUID | null;
  createdAt?: string;
}

export interface CommentCreationDTO {
  contentEntityId: UUID;
  isAReply: boolean;
  originalCommentId?: UUID | null;
  content: string;
  type: "POST" | "SHORT";
}

export interface FollowDTO {
  follower: ShortProfileDTO;
  followed: ShortProfileDTO;
  createdAt: string;
}

export function useSocialsApi() {
  const auth = useAuth();

  const token = auth.user?.access_token;

  async function request<T>(path: string, init?: RequestInit): Promise<T> {
    const res = await fetch(`${GATEWAY_BASE}${path}` , {
      ...init,
      headers: {
        "Content-Type": "application/json",
        ...(init?.headers || {}),
        Authorization: `Bearer ${token}`,
      },
      credentials: "include",
    });
    if (!res.ok) {
      const text = await res.text().catch(() => "");
      throw new Error(`${res.status} ${res.statusText}: ${text}`);
    }
    if (res.status === 204) return undefined as unknown as T;
    return (await res.json()) as T;
  }

  function buildExcludeQS(excludeIds?: UUID[]): string {
    if (!excludeIds || excludeIds.length === 0) return "";
    const params = excludeIds.map((id) => `&exclude=${encodeURIComponent(id)}`).join("");
    return params;
  }

  return {
    // Profiles
    getCurrentProfile: () => request<ProfileDTO>(`/profile`),
    getProfileByTag: (tag: string) => request<ProfileDTO>(`/profile/get-by-tag/${encodeURIComponent(tag)}`),
    patchProfile: (dto: ProfileDTO) => request<void>(`/profile/patch-profile`, { method: "PATCH", body: JSON.stringify(dto) }),
    patchTag: (tag: string) => request<void>(`/profile/patch-tag`, { method: "PATCH", body: JSON.stringify({ tag }) }),

    // Posts
    getPostsByProfileId: (profileId: UUID, page = 0, size = 10) => request<PostDTO[]>(`/posts/get-posts/${profileId}?page=${page}&size=${size}&ts=${Date.now()}`),
    getPostById: (postId: UUID) => request<PostDTO>(`/posts/get-post/${postId}?ts=${Date.now()}`),
    createPost: (payload: PostCreationDTO) => request<void>(`/posts/create-post`, { method: "POST", body: JSON.stringify(payload) }),

    // Likes
    like: (contentEntityId: UUID) => request<void>(`/likes/like`, { method: "POST", body: JSON.stringify({ contentEntityId }) }),
    unlike: (contentEntityId: UUID) => request<void>(`/likes/unlike`, { method: "DELETE", body: JSON.stringify({ contentEntityId }) }),

    // Follows
    follow: (followedId: UUID) => request<void>(`/follows/follow`, { method: "POST", body: JSON.stringify(followedId) }),
    unfollow: (unfollowedId: UUID) => request<void>(`/follows/unfollow`, { method: "DELETE", body: JSON.stringify(unfollowedId) }),
    getFollowing: (profileId: UUID, page = 0, size = 20) => request<FollowDTO[]>(`/follows/get-follows/${profileId}?page=${page}&size=${size}`),
    getFollowers: (profileId: UUID, page = 0, size = 20) => request<FollowDTO[]>(`/follows/get-followed-by/${profileId}?page=${page}&size=${size}`),
    // Follow state
    checkFollow: (profileId: UUID) => request<{ following: boolean }>(`/follows/check-follow/${profileId}`, { method: "GET" }),

    // Comments
    getComments: (contentEntityId: UUID, page = 0, size = 10) => request<CommentDTO[]>(`/comments/get-comments/${contentEntityId}?page=${page}&size=${size}`),
    getReplies: (commentId: UUID, page = 0, size = 10) => request<CommentDTO[]>(`/comments/get-replies/${commentId}?page=${page}&size=${size}`),
    createComment: (payload: CommentCreationDTO) => request<void>(`/comments/create-comment`, { method: "POST", body: JSON.stringify(payload) }),

    // Shorts
    getShorts: (size = 5, excludeIds?: UUID[]) => request<ShortDTO[]>(`/shorts?shorts_size=${size}${buildExcludeQS(excludeIds)}&ts=${Date.now()}`),
  };
}

// Shorts DTOs
export interface ShortElementDTO { id: UUID; type: string; url: string; orderIndex: number; createdAt?: string }
export interface ShortTagDTO { id: UUID; name: string }
export interface ShortProfileDTO extends ProfileDTO { userId?: number; status?: string }
export interface ShortDTO {
  id: UUID;
  profile: ShortProfileDTO;
  type: string;
  elements: ShortElementDTO[];
  tags: ShortTagDTO[];
  likesAmount: number;
  views: number;
  description?: string;
  isPublic?: boolean;
  createdAt?: string;
}
