// src/pages/Login.js
import { useAuth } from "react-oidc-context";
import { useNavigate } from "react-router-dom";
import {useEffect} from "react";

export default function Login() {
    const auth = useAuth();
    const navigate = useNavigate();

    useEffect(() => {
        if (auth.activeNavigator === "signinSilent") {
            return;
        }
        if (auth.isAuthenticated) {
            navigate("/");
        }
    }, [auth, navigate]);

    if (auth.isLoading) return <div>Loading...</div>;
    if (auth.error) return <div>Oops... {auth.error.message}</div>;

    return (
        <button onClick={() => void auth.signinRedirect()}>
            Log in
        </button>
    );
}
