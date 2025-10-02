import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import Home from "./components/Home";
import Login from "./components/Login";
import { useAuth } from "react-oidc-context";
import CallbackPage from "./components/util/CallbackPage.tsx";
import LogoutPage from "./components/util/LogoutPage.tsx";
import ProfilePage from "./components/socials/ProfilePage";
import PostDetailPage from "./components/socials/PostDetailPage";
import ShortsPage from "./components/socials/ShortsPage";

function App() {
    const auth = useAuth();

    if (auth.activeNavigator === "signinSilent") return <div>Signing you in...</div>;
    if (auth.activeNavigator === "signoutRedirect") return <div>Signing you out...</div>;
    if (auth.isLoading) return <div>Loading...</div>;
    if (auth.error) return <div>Oops... {auth.error.message}</div>;

    return (
        <Router>
            <Routes>
                <Route path="/" element={<Home />} />
                <Route path="/callback" element={<CallbackPage />} />
                <Route path="/logout" element={<LogoutPage />} />
                <Route path="/login" element={<Login />} />

                {/* Socials */}
                <Route path="/socials" element={<Navigate to="/socials/profile" replace />} />
                <Route path="/socials/profile" element={<ProfilePage />} />
                <Route path="/socials/profile/:tag" element={<ProfilePage />} />
                <Route path="/socials/posts/:postId" element={<PostDetailPage />} />
                <Route path="/socials/shorts" element={<ShortsPage />} />
            </Routes>
        </Router>
    );
}

export default App;
