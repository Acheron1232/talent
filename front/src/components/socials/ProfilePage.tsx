import {useEffect, useMemo, useState} from "react";
import {Link, useNavigate, useParams} from "react-router-dom";
import { useSocialsApi } from "./api";
import type { PostDTO, PostCreationDTO, ProfileDTO } from "./api";
import { useAuth } from "react-oidc-context";
import CommentsThread from "./CommentsThread";

export default function ProfilePage() {
  const { tag } = useParams<{ tag?: string }>();
  const api = useSocialsApi();
  const auth = useAuth();
  const navigate = useNavigate();

  const [profile, setProfile] = useState<ProfileDTO | null>(null);
  const [posts, setPosts] = useState<PostDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [newPostText, setNewPostText] = useState("");
  const [repostOf, setRepostOf] = useState<string | null>(null);

  const isOwnProfile = useMemo(() => !tag, [tag]);

  useEffect(() => {
    let mounted = true;
    (async () => {
      try {
        setLoading(true);
        setError(null);
        const p = tag ? await api.getProfileByTag(tag) : await api.getCurrentProfile();
        if (!mounted) return;
        setProfile(p);
        if (p.id) {
          const postsResp = await api.getPostsByProfileId(p.id, 0, 20);
          if (!mounted) return;
          setPosts(postsResp);
        }
      } catch (e: any) {
        setError(e.message || String(e));
      } finally {
        setLoading(false);
      }
    })();
    return () => { mounted = false; };
  }, [tag]);

  async function createPost(payload: PostCreationDTO) {
    await api.createPost(payload);
    // refresh posts
    if (profile?.id) {
      const postsResp = await api.getPostsByProfileId(profile.id, 0, 20);
      setPosts(postsResp);
    }
    // clear state
    setNewPostText("");
    setRepostOf(null);
  }

  async function onLikeToggle(post: PostDTO) {
    try {
      // naive optimistic toggle: if likesAmount undefined, just call like
      await api.like(post.id);
      const updated = posts.map(p => p.id === post.id ? { ...p, likesAmount: (p.likesAmount ?? 0) + 1 } : p);
      setPosts(updated);
    } catch {
      // attempt unlike if already liked (no explicit state, backend will guard)
      try {
        await api.unlike(post.id);
        const updated = posts.map(p => p.id === post.id ? { ...p, likesAmount: Math.max(0, (p.likesAmount ?? 0) - 1) } : p);
        setPosts(updated);
      } catch (e) {
        console.error(e);
      }
    }
  }

  if (!auth.isAuthenticated) {
    return (
      <div>
        <p>You must log in to use Socials.</p>
        <button onClick={() => auth.signinRedirect()}>Log in</button>
      </div>
    );
  }

  if (loading) return <div>Loading profile...</div>;
  if (error) return <div>Error: {error}</div>;
  if (!profile) return <div>No profile found.</div>;

  return (
    <div style={{ maxWidth: 800, margin: "0 auto", padding: 16 }}>
      <div style={{ display: "flex", alignItems: "center", gap: 12 }}>
        {profile.profilePictureUrl && (
          <img src={profile.profilePictureUrl} alt="avatar" width={56} height={56} style={{ borderRadius: "50%", objectFit: "cover" }} />
        )}
        <div>
          <div style={{ fontSize: 20, fontWeight: 700 }}>{profile.displayName || profile.tag}</div>
          <div style={{ color: "#666" }}>@{profile.tag}</div>
        </div>
      </div>

      {/* Patch profile/tag minimal UI */}
      {isOwnProfile && (
        <div style={{ marginTop: 16, padding: 12, border: "1px solid #eee", borderRadius: 8 }}>
          <button onClick={async () => {
            const newDisplayName = prompt("New display name", profile.displayName || "");
            if (newDisplayName == null) return;
            await api.patchProfile({ displayName: newDisplayName });
            const refreshed = await api.getCurrentProfile();
            setProfile(refreshed);
          }}>Edit profile</button>
          <button style={{ marginLeft: 8 }} onClick={async () => {
            const newTag = prompt("New tag (without @)", profile.tag || "");
            if (!newTag) return;
            await api.patchTag(newTag);
            const refreshed = await api.getCurrentProfile();
            setProfile(refreshed);
            navigate(`/socials/profile/${encodeURIComponent(newTag)}`);
          }}>Edit tag</button>
        </div>
      )}

      {/* Create / Repost */}
      {(isOwnProfile || repostOf) && (
        <div style={{ marginTop: 16 }}>
          <h3>{repostOf ? "Repost" : "Create a post"}</h3>
          <textarea
            placeholder={repostOf ? "Add your thoughts (optional)" : "What's happening?"}
            value={newPostText}
            onChange={(e) => setNewPostText(e.target.value)}
            rows={3}
            style={{ width: "100%" }}
          />
          <div style={{ marginTop: 8 }}>
            <button onClick={async () => {
              const payload: PostCreationDTO = repostOf
                ? { reposted: true, originalPostId: repostOf, textContent: newPostText || undefined }
                : { reposted: false, textContent: newPostText || undefined };
              await createPost(payload);
            }}>{repostOf ? "Repost" : "Post"}</button>
            {repostOf && (
              <button style={{ marginLeft: 8 }} onClick={() => setRepostOf(null)}>Cancel repost</button>
            )}
          </div>
        </div>
      )}

      {/* Posts list */}
      <div style={{ marginTop: 24 }}>
        <h3>Posts</h3>
        {posts.length === 0 && <div>No posts yet.</div>}
        {posts.map((p) => (
          <div key={p.id} style={{ border: "1px solid #eee", borderRadius: 8, padding: 12, marginBottom: 12 }}>
            <div style={{ display: "flex", justifyContent: "space-between" }}>
              <div>
                <Link to={`/socials/profile/${p.profile?.tag ?? ""}`}>@{p.profile?.tag}</Link>
                {p.reposted && p.originalPost && (
                  <div style={{ color: "#999" }}>Reposted from @{p.originalPost.profile?.tag}</div>
                )}
              </div>
              <div style={{ color: "#666" }}>{new Date(p.createdAt || "").toLocaleString()}</div>
            </div>
            <div style={{ marginTop: 8, whiteSpace: "pre-wrap" }}>{p.textContent}</div>
            {p.reposted && p.originalPost && (
              <div style={{ marginTop: 8, padding: 8, borderLeft: "3px solid #ddd", background: "#fafafa" }}>
                <div style={{ fontSize: 13, color: "#666" }}>Original by @{p.originalPost.profile?.tag}</div>
                <div style={{ whiteSpace: "pre-wrap" }}>{p.originalPost.textContent}</div>
              </div>
            )}
            <div style={{ marginTop: 8, display: "flex", gap: 8 }}>
              <button onClick={() => onLikeToggle(p)}>Like ({p.likesAmount ?? 0})</button>
              <button onClick={() => navigate(`/socials/posts/${p.id}`)}>Open</button>
              <button onClick={() => setRepostOf(p.id)}>Repost</button>
            </div>
            <div>
              <CommentsThread contentEntityId={p.id} type="POST" />
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
