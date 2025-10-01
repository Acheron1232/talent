// src/pages/Home.js
import { useAuth } from "react-oidc-context";
import { useNavigate } from "react-router-dom";
import {jwtDecode} from "jwt-decode";

export default function Home() {
    const auth = useAuth();
    const navigate = useNavigate();
    // useEffect(() => {
    //     if (!auth.isAuthenticated && !auth.isLoading) {
    //         navigate("/login");
    //     }
    // }, [auth.isAuthenticated, auth.isLoading, navigate]);
    const fetchProtected = async () => {
        try {
            const res = await fetch("http://localhost:8080/books", {
                headers: {
                    Authorization: `Bearer ${auth.user?.access_token}`,
                },
                credentials: "include"
            });
            const data = await res.text();
            console.log(data);
        } catch (e) {

            console.error("Failed to fetch", e);
        }
    };

    const handleLogout = async () => {
        await fetch(auth.settings.authority + "/spa/logout", {
            headers: {
                Authorization: `Bearer ${auth.user?.access_token}`,
            },
            credentials: "include",
        });
        await auth.signoutRedirect({
            post_logout_redirect_uri: "http://localhost:5173/logout",
        });
        await auth.removeUser();
        // navigate("/login");
    };
    interface MyClaims {
        name?: string;
        sub?: string;
        roles?: string[];
        [key: string]: any; // якщо є інші поля
    }
    const name = () : string=>{

        const token = auth.user?.access_token;

        if (token) {
            const claims = jwtDecode<MyClaims>(token);
            return claims.name ?? "";
        }
        return "";
    }

    return (
        <div>
            {auth.isAuthenticated?(
                <>
                    Hello {name()}{" "}
                    <button onClick={handleLogout}>Log out</button>
                    <button onClick={fetchProtected}>Fetch protected resource</button>
                    <button onClick={() => navigate("/socials")}>Enter Socials</button>
                </>
                ):(
                <>
                    <button onClick={() => auth.signinRedirect()}>Log in</button>
                </>
                )}
        </div>
    );
}
