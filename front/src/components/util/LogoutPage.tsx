import { useEffect } from "react";
import { useAuth } from "react-oidc-context";
import { useNavigate } from "react-router-dom";

export default function LogoutPage() {
    const auth = useAuth();
    const navigate = useNavigate();

    useEffect(() => {
        if (!auth.isLoading) {
            navigate("/"); // 🚀 завжди йдемо на home
        }
    }, [auth.isLoading, navigate]);

    return <p>Loading...</p>;
}
