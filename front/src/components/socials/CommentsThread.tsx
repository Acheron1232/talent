import React, { useEffect, useState } from "react";
import { useSocialsApi } from "./api";
import type { UUID, CommentDTO } from "./api";

interface CommentsThreadProps {
  contentEntityId: UUID;
  type: "POST" | "SHORT";
}

interface CommentItemProps {
  comment: CommentDTO;
}

export default function CommentsThread({ contentEntityId, type }: CommentsThreadProps) {
  const api = useSocialsApi();
  const [comments, setComments] = useState<CommentDTO[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [input, setInput] = useState("");
  const [expanded, setExpanded] = useState(false);

  async function load() {
    try {
      setLoading(true);
      setError(null);
      const cs = await api.getComments(contentEntityId, 0, 20);
      setComments(cs);
    } catch (e: any) {
      setError(e.message || String(e));
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    if (expanded) {
      load();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [expanded, contentEntityId]);

  async function onSubmit() {
    const text = input.trim();
    if (!text) return;
    await api.createComment({ contentEntityId, isAReply: false, originalCommentId: null, content: text, type });
    setInput("");
    await load();
  }

  return (
    <div style={{ marginTop: 8 }}>
      <button onClick={() => setExpanded((e) => !e)}>{expanded ? "Hide comments" : "Comments"}</button>
      {expanded && (
        <div style={{ marginTop: 8 }}>
          <div style={{ display: "flex", gap: 8, marginBottom: 8 }}>
            <input
              placeholder="Write a comment..."
              value={input}
              onChange={(e) => setInput(e.target.value)}
              style={{ flex: 1 }}
            />
            <button onClick={onSubmit}>Send</button>
          </div>
          {loading && <div>Loading comments...</div>}
          {error && <div style={{ color: "#c00" }}>Error: {error}</div>}
          {comments.length === 0 && !loading && <div>No comments yet.</div>}
          {comments.map((c) => (
            <CommentItem key={c.id} comment={c} />
          ))}
        </div>
      )}
    </div>
  );
}

function CommentItem({ comment }: CommentItemProps) {
  const api = useSocialsApi();
  const [showReplies, setShowReplies] = useState(false);
  const [replies, setReplies] = useState<CommentDTO[]>([]);
  const [loadingReplies, setLoadingReplies] = useState(false);
  const [replyInput, setReplyInput] = useState("");

  async function loadReplies() {
    try {
      setLoadingReplies(true);
      const rs = await api.getReplies(comment.id, 0, 20);
      setReplies(rs);
    } finally {
      setLoadingReplies(false);
    }
  }

  async function submitReply() {
    const text = replyInput.trim();
    if (!text) return;
    await api.createComment({
      contentEntityId: comment.contentEntityId,
      isAReply: true,
      originalCommentId: comment.id,
      content: text,
      // We do not have type on comment; backend can infer by contentEntityId; omit or cast as any
      // For safety, pass POST by default; backend uses content entity type anyway
      type: "POST",
    } as any);
    setReplyInput("");
    await loadReplies();
  }

  return (
    <div style={{ borderTop: "1px solid #eee", padding: "8px 0 8px 0", marginLeft: 0 }}>
      <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
        {comment.profile?.profilePictureUrl && (
          <img src={comment.profile.profilePictureUrl} alt="avatar" width={24} height={24} style={{ borderRadius: "50%" }} />
        )}
        <div style={{ fontWeight: 600 }}>{comment.profile?.displayName || comment.profile?.tag || "@user"}</div>
        <div style={{ fontSize: 12, color: "#666" }}>{new Date(comment.createdAt || "").toLocaleString()}</div>
      </div>
      <div style={{ marginTop: 4, whiteSpace: "pre-wrap" }}>{comment.content}</div>
      <div style={{ marginTop: 4 }}>
        <span
          style={{ color: "#555", cursor: "pointer", textDecoration: "underline" }}
          onClick={async () => {
            const next = !showReplies;
            setShowReplies(next);
            if (next && replies.length === 0) {
              await loadReplies();
            }
          }}
        >
          replies
        </span>
      </div>
      {showReplies && (
        <div style={{ marginTop: 8, marginLeft: 16 }}>
          <div style={{ display: "flex", gap: 8, marginBottom: 8 }}>
            <input
              placeholder="Write a reply..."
              value={replyInput}
              onChange={(e) => setReplyInput(e.target.value)}
              style={{ flex: 1 }}
            />
            <button onClick={submitReply}>Reply</button>
          </div>
          {loadingReplies && <div>Loading replies...</div>}
          {replies.map((r) => (
            <div key={r.id} style={{ marginBottom: 8 }}>
              <CommentItem comment={r} />
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
