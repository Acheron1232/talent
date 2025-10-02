import { useEffect, useMemo, useRef, useState } from "react";
import { useAuth } from "react-oidc-context";
import { useSocialsApi } from "./api";
import type { ShortDTO } from "./api";
// Simple TikTok-like vertical feed
export default function ShortsPage() {
  const auth = useAuth();
  const api = useSocialsApi();

  const [shorts, setShorts] = useState<ShortDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isFetchingMore, setIsFetchingMore] = useState(false);
  const [viewedSinceLastFetch, setViewedSinceLastFetch] = useState(0);

  // Track which short was already counted as viewed to avoid duplicate counting
  const viewedSetRef = useRef<Set<string>>(new Set());
  // Track IDs we've already rendered to avoid duplicate keys when appending
  const seenIdsRef = useRef<Set<string>>(new Set());
  // Global mute toggle: default muted for autoplay; user can enable sound
  const [globalMuted, setGlobalMuted] = useState(true);

  const videosRef = useRef<Map<string, HTMLVideoElement>>(new Map());

  const observer = useMemo(() => {
    // Observe visibility to autoplay/pause and mark as viewed
    const options = { threshold: [0.6] }; // 60% visible
    const io = new IntersectionObserver((entries) => {
      entries.forEach((entry) => {
        const el = entry.target as HTMLVideoElement;
        const id = el.dataset["shortId"] || "";
        if (entry.isIntersecting && entry.intersectionRatio >= 0.6) {
          // Apply current mute preference and play when in view
          el.muted = globalMuted;
          el.play().catch(() => {/* ignore */});
          // Count as viewed once
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
  }, [globalMuted]);

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
    // Attach observer to videos
    shorts.forEach((s) => {
      const v = videosRef.current.get(s.id);
      if (v) observer.observe(v);
    });
    return () => {
      observer.disconnect();
    };
  }, [shorts, observer]);

  useEffect(() => {
    // When 4 have been viewed since last fetch, fetch 5 more
    async function maybeFetchMore() {
      if (isFetchingMore) return;
      if (viewedSinceLastFetch < 4) return;
      try {
        setIsFetchingMore(true);
        const data = await api.getShorts();
        setShorts((prev) => {
          const seen = new Set(seenIdsRef.current);
          const filtered = data.filter(d => !seen.has(d.id));
          filtered.forEach(d => seen.add(d.id));
          seenIdsRef.current = seen;
          return [...prev, ...filtered];
        });
        setViewedSinceLastFetch(0); // reset counter after fetch
      } catch (e) {
        // non-fatal
        console.error(e);
      } finally {
        setIsFetchingMore(false);
      }
    }
    maybeFetchMore();
  }, [viewedSinceLastFetch, isFetchingMore, api]);

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
      {shorts.map((s) => {
        const videoUrl = s.elements?.find((e) => e.type === "VIDEO")?.url || "";
        return (
          <section
            key={s.id}
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
              <video
                ref={(el) => {
                  if (el) {
                    videosRef.current.set(s.id, el);
                    el.dataset["shortId"] = s.id;
                    el.muted = globalMuted;
                    el.playsInline = true;
                    el.loop = true;
                    el.preload = "metadata";
                  } else {
                    videosRef.current.delete(s.id);
                  }
                }}
                src={videoUrl}
                style={{ width: "100vw", height: "100vh", objectFit: "cover" }}
                controls={false}
                onClick={() => {
                  // Per-video quick toggle
                  const el = videosRef.current.get(s.id);
                  if (!el) return;
                  const nextMuted = !el.muted;
                  el.muted = nextMuted;
                  // If just unmuted, try to continue playback
                  if (!nextMuted) {
                    el.play().catch(() => {});
                  }
                  // Also update global preference if user explicitly toggles
                  setGlobalMuted(nextMuted);
                }}
              />
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
            </div>

            {/* Sound toggle button */}
            {videoUrl && (
              <button
                style={{ position: "absolute", top: 16, right: 16, padding: "6px 10px", background: "rgba(0,0,0,0.5)", color: "#fff", border: "none", borderRadius: 6, cursor: "pointer" }}
                onClick={() => {
                  const next = !globalMuted;
                  setGlobalMuted(next);
                  const el = videosRef.current.get(s.id);
                  if (el) {
                    el.muted = next;
                    if (!next) el.play().catch(() => {});
                  }
                }}
              >
                {globalMuted ? "Sound Off" : "Sound On"}
              </button>
            )}
          </section>
        );
      })}

      {isFetchingMore && (
        <div style={{ position: "sticky", bottom: 0, width: "100%", textAlign: "center", padding: 8, background: "rgba(0,0,0,0.3)" }}>
          Loading more...
        </div>
      )}
    </div>
  );
}
