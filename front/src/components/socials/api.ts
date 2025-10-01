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
  postId: UUID;
  content: string;
  createdAt?: string;
}

export interface CommentCreationDTO {
  postId: UUID;
  isAReply: boolean;
  originalCommentId?: UUID | null;
  content: string;
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

  return {
    // Profiles
    getCurrentProfile: () => request<ProfileDTO>(`/profile`),
    getProfileByTag: (tag: string) => request<ProfileDTO>(`/profile/get-by-tag/${encodeURIComponent(tag)}`),
    patchProfile: (dto: ProfileDTO) => request<void>(`/profile/patch-profile`, { method: "PATCH", body: JSON.stringify(dto) }),
    patchTag: (tag: string) => request<void>(`/profile/patch-tag`, { method: "PATCH", body: JSON.stringify({ tag }) }),

    // Posts
    getPostsByProfileId: (profileId: UUID, page = 0, size = 10) => request<PostDTO[]>(`/posts/get-posts/${profileId}?page=${page}&size=${size}`),
    getPostById: (postId: UUID) => request<PostDTO>(`/posts/get-post/${postId}`),
    createPost: (payload: PostCreationDTO) => request<void>(`/posts/create-post`, { method: "POST", body: JSON.stringify(payload) }),

    // Likes
    like: (postId: UUID) => request<void>(`/likes/like`, { method: "POST", body: JSON.stringify({ postId }) }),
    unlike: (postId: UUID) => request<void>(`/likes/unlike`, { method: "DELETE", body: JSON.stringify({ postId }) }),

    // Comments
    getComments: (postId: UUID, page = 0, size = 10) => request<CommentDTO[]>(`/comments/get-comments/${postId}/?page=${page}&size=${size}`),
    createComment: (payload: CommentCreationDTO) => request<void>(`/comments/create-comment`, { method: "POST", body: JSON.stringify(payload) }),
  };
}
