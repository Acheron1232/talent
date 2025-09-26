import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Home from "./components/Home";
import Login from "./components/Login";
import { useAuth } from "react-oidc-context";
import CallbackPage from "./components/util/CallbackPage.tsx";
import LogoutPage from "./components/util/LogoutPage.tsx";

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
            </Routes>
        </Router>
    );
}

export default App;
