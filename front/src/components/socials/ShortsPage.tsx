import { useEffect, useMemo, useRef, useState } from "react";
import { useAuth } from "react-oidc-context";
import { useSocialsApi } from "./api";
import type { ShortDTO } from "./api";
import CommentsThread from "./CommentsThread";
// Simple TikTok-like vertical feed
export default function ShortsPage() {
  const auth = useAuth();
  const api = useSocialsApi();

  const [shorts, setShorts] = useState<ShortDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isFetchingMore, setIsFetchingMore] = useState(false);
  const [viewedSinceLastFetch, setViewedSinceLastFetch] = useState(0);
  const bottomSentinelRef = useRef<HTMLDivElement | null>(null);

  // Track which short was already counted as viewed to avoid duplicate counting
  const viewedSetRef = useRef<Set<string>>(new Set());
  // Track IDs we've already rendered to avoid duplicate keys when appending
  const seenIdsRef = useRef<Set<string>>(new Set());
  // Global mute toggle: default muted for autoplay; user can enable sound
  const [globalMuted, setGlobalMuted] = useState(true);
  // Only the currently active (in-view) video should have sound
  const [activeKey, setActiveKey] = useState<string | null>(null);

  const videosRef = useRef<Map<string, HTMLVideoElement>>(new Map());

  const observer = useMemo(() => {
    // Observe visibility to determine the active video and mark as viewed
    const options = { threshold: [0.6] }; // 60% visible
    const io = new IntersectionObserver((entries) => {
      entries.forEach((entry) => {
        const el = entry.target as HTMLVideoElement;
        const id = el.dataset["shortId"] || "";
        const key = el.dataset["shortKey"] || "";
        if (entry.isIntersecting && entry.intersectionRatio >= 0.6) {
          // Make this video active and play it
          if (key) setActiveKey(key);
          el.play().catch(() => {/* ignore */});
          // Count as viewed once (by content id)
          if (id && !viewedSetRef.current.has(id)) {
            viewedSetRef.current.add(id);
            setViewedSinceLastFetch((c) => c + 1);
          }
        } else {
          // Pause when out of view
          el.pause();
        }
      });
    }, options);
    return io;
  }, []);

  useEffect(() => {
    // Initial load
    let mounted = true;
    (async () => {
      try {
        setLoading(true);
        setError(null);
        const data = await api.getShorts();
        if (!mounted) return;
        setShorts(data);
        // initialize seen IDs
        seenIdsRef.current = new Set(data.map(d => d.id));
        viewedSetRef.current.clear();
        setViewedSinceLastFetch(0);
      } catch (e: any) {
        setError(e.message || String(e));
      } finally {
        setLoading(false);
      }
    })();
    return () => { mounted = false; };
  }, []);

  useEffect(() => {
    // Attach observer to all current video refs
    videosRef.current.forEach((v) => {
      if (v) observer.observe(v);
    });
    return () => {
      observer.disconnect();
    };
  }, [shorts, observer]);

  // Ensure only the active video has sound; others are muted and paused
  useEffect(() => {
    videosRef.current.forEach((vid, key) => {
      if (key === activeKey) {
        vid.muted = globalMuted;
        vid.play().catch(() => {});
      } else {
        vid.muted = true;
        vid.pause();
      }
    });
  }, [activeKey, globalMuted]);

  // Bottom sentinel intersection to load more
  useEffect(() => {
    if (!bottomSentinelRef.current) return;
    const el = bottomSentinelRef.current;
    const io = new IntersectionObserver(async (entries) => {
      entries.forEach(async (entry) => {
        if (!entry.isIntersecting) return;
        if (isFetchingMore) return;
        try {
          setIsFetchingMore(true);
          const data = await api.getShorts(5);
          setShorts((prev) => [...prev, ...data]);
        } catch (e) {
          console.error(e);
        } finally {
          setIsFetchingMore(false);
        }
      });
    }, { threshold: 0.1 });
    io.observe(el);
    return () => io.disconnect();
  }, [api, isFetchingMore]);

  if (!auth.isAuthenticated) {
    return (
      <div style={{ height: "100vh", display: "flex", alignItems: "center", justifyContent: "center", flexDirection: "column" }}>
        <p>You must log in to watch shorts.</p>
        <button onClick={() => auth.signinRedirect()}>Log in</button>
      </div>
    );
  }

  if (loading) return <div style={{ height: "100vh", display: "grid", placeItems: "center" }}>Loading shorts...</div>;
  if (error) return <div style={{ padding: 16 }}>Error: {error}</div>;

  return (
    <div
      style={{
        width: "100vw",
        height: "100vh",
        overflowY: "auto",
        overflowX: "hidden",
        scrollSnapType: "y mandatory",
        background: "#000",
        color: "#fff",
      }}
    >
      {shorts.map((s, idx) => {
        const itemKey = `${s.id}-${idx}`;
        const videoUrl = s.elements?.find((e) => e.type === "VIDEO")?.url || "";
        return (
          <section
            key={`${s.id}-${idx}`}
            style={{
              width: "100vw",
              height: "100vh",
              position: "relative",
              scrollSnapAlign: "start",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              backgroundColor: "#000",
              overflow: "hidden",
            }}
          >
            {videoUrl ? (
              <div
                style={{
                  width: "min(100vw, 480px)",
                  height: "100vh",
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                  position: "relative",
                }}
              >
                <video
                  ref={(el) => {
                    if (el) {
                      videosRef.current.set(itemKey, el);
                      el.dataset["shortId"] = s.id;
                      el.dataset["shortKey"] = itemKey;
                      el.muted = globalMuted;
                      el.playsInline = true;
                      el.loop = true;
                      el.preload = "metadata";
                    } else {
                      const prev = videosRef.current.get(itemKey);
                      if (prev) {
                        try { prev.muted = true; prev.pause(); } catch {}
                      }
                      videosRef.current.delete(itemKey);
                    }
                  }}
                  src={videoUrl}
                  style={{ width: "100%", height: "100%", objectFit: "contain", backgroundColor: "#000" }}
                  controls={false}
                  onClick={() => {
                    // Make this the active video and toggle global sound
                    setActiveKey(itemKey);
                    const next = !globalMuted;
                    setGlobalMuted(next);
                  }}
                />
              </div>
            ) : (
              <div style={{ color: "#fff" }}>No video</div>
            )}

            {/* Overlay info */}
            <div style={{ position: "absolute", bottom: 16, left: 16, right: 16 }}>
              <div style={{ fontWeight: 700 }}>{s.profile?.displayName || s.profile?.tag || "Anonymous"}</div>
              {s.description && <div style={{ marginTop: 4, opacity: 0.9 }}>{s.description}</div>}
              {s.tags?.length > 0 && (
                <div style={{ marginTop: 6, fontSize: 12, opacity: 0.8 }}>
                  {s.tags.map((t) => `#${t.name}`).join(" ")}
                </div>
              )}
              {/* Actions */}
              <div style={{ marginTop: 8, display: "flex", gap: 8, alignItems: "center" }}>
                <button
                  style={{ padding: "6px 10px", background: "rgba(0,0,0,0.5)", color: "#fff", border: "1px solid rgba(255,255,255,0.2)", borderRadius: 6 }}
                  onClick={async () => {
                    try {
                      await api.like(s.id);
                      setShorts(prev => prev.map(item => item.id === s.id ? { ...item, likesAmount: (item.likesAmount ?? 0) + 1 } : item));
                    } catch {
                      try {
                        await api.unlike(s.id);
                        setShorts(prev => prev.map(item => item.id === s.id ? { ...item, likesAmount: Math.max(0, (item.likesAmount ?? 0) - 1) } : item));
                      } catch (e) { console.error(e); }
                    }
                  }}
                >Like ({s.likesAmount ?? 0})</button>
                <div>
                  <CommentsThread contentEntityId={s.id} type="SHORT" />
                </div>
              </div>
            </div>

            {/* Sound toggle button */}
            {videoUrl && (
              <button
                style={{ position: "absolute", top: 16, right: 16, padding: "6px 10px", background: "rgba(0,0,0,0.5)", color: "#fff", border: "none", borderRadius: 6, cursor: "pointer" }}
                onClick={() => {
                  // Toggle global sound and ensure this is the active video
                  setActiveKey(itemKey);
                  const next = !globalMuted;
                  setGlobalMuted(next);
                }}
              >
                {globalMuted ? "Sound Off" : "Sound On"}
              </button>
            )}
          </section>
        );
      })}

      <div ref={bottomSentinelRef} style={{ height: 1 }} />

      {isFetchingMore && (
        <div style={{ position: "sticky", bottom: 0, width: "100%", textAlign: "center", padding: 8, background: "rgba(0,0,0,0.3)" }}>
          Loading more...
        </div>
      )}
    </div>
  );
}
