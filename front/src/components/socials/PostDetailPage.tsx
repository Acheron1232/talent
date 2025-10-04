import {useEffect, useState} from "react";
import {Link, useParams} from "react-router-dom";
import { useSocialsApi } from "./api";
import type { PostDTO } from "./api";
import { useAuth } from "react-oidc-context";
import CommentsThread from "./CommentsThread";

export default function PostDetailPage() {
  const { postId } = useParams<{ postId: string }>();
  const api = useSocialsApi();
  const auth = useAuth();

  const [post, setPost] = useState<PostDTO | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let mounted = true;
    (async () => {
      try {
        setLoading(true);
        setError(null);
        if (!postId) return;
        const p = await api.getPostById(postId);
        if (!mounted) return;
        setPost(p);
      } catch (e: any) {
        setError(e.message || String(e));
      } finally {
        setLoading(false);
      }
    })();
    return () => { mounted = false; };
  }, [postId]);

  async function onLike() {
    if (!post) return;
    try {
      await api.like(post.id);
      setPost({ ...post, likesAmount: (post.likesAmount ?? 0) + 1 });
    } catch {
      try {
        await api.unlike(post.id);
        setPost({ ...post, likesAmount: Math.max(0, (post.likesAmount ?? 0) - 1) });
      } catch (e) {
        console.error(e);
      }
    }
  }

  async function onCommentSubmit() {
    if (!postId || !commentInput.trim()) return;
    await api.createComment({ postId, isAReply: false, originalCommentId: null, content: commentInput.trim() });
    setCommentInput("");
    const cs = await api.getComments(postId, 0, 20);
    setComments(cs);
  }

  if (!auth.isAuthenticated) {
    return (
      <div>
        <p>You must log in to use Socials.</p>
        <button onClick={() => auth.signinRedirect()}>Log in</button>
      </div>
    );
  }

  if (loading) return <div>Loading post...</div>;
  if (error) return <div>Error: {error}</div>;
  if (!post) return <div>Post not found.</div>;

  return (
    <div style={{ maxWidth: 800, margin: "0 auto", padding: 16 }}>
      <div style={{ border: "1px solid #eee", borderRadius: 8, padding: 12 }}>
        <div style={{ display: "flex", justifyContent: "space-between" }}>
          <div>
            <Link to={`/socials/profile/${post.profile?.tag ?? ""}`}>@{post.profile?.tag}</Link>
            {post.reposted && post.originalPost && (
              <div style={{ color: "#999" }}>Reposted from @{post.originalPost.profile?.tag}</div>
            )}
          </div>
          <div style={{ color: "#666" }}>{new Date(post.createdAt || "").toLocaleString()}</div>
        </div>
        <div style={{ marginTop: 8, whiteSpace: "pre-wrap" }}>{post.textContent}</div>
        {post.reposted && post.originalPost && (
          <div style={{ marginTop: 8, padding: 8, borderLeft: "3px solid #ddd", background: "#fafafa" }}>
            <div style={{ fontSize: 13, color: "#666" }}>Original by @{post.originalPost.profile?.tag}</div>
            <div style={{ whiteSpace: "pre-wrap" }}>{post.originalPost.textContent}</div>
          </div>
        )}
        <div style={{ marginTop: 8 }}>
          <button onClick={onLike}>Like ({post.likesAmount ?? 0})</button>
        </div>
      </div>

      <div style={{ marginTop: 16 }}>
        <h3>Comments</h3>
        {post && <CommentsThread contentEntityId={post.id} type="POST" />}
      </div>
    </div>
  );
}
