import { useEffect } from "react";
import { useLocation, useNavigate } from "react-router";
import axios from "axios";

function OAuth2Callback() {
    const location = useLocation();
    const navigate = useNavigate();

    useEffect(() => {
        const searchParams = new URLSearchParams(location.search);
        const code = searchParams.get("code");

        if (code) {
            // Сделай POST-запрос, чтобы получить access_token
            axios
                .post(
                    "http://localhost:9000/oauth2/token",
                    {
                        code: code,
                        redirect_uri: "http://localhost:3000/oauth2/callback",
                        grant_type: "authorization_code",
                    },
                    {
                        headers: {
                            "Content-Type": "application/x-www-form-urlencoded",
                        },
                        auth: {
                            username: "client",
                            password: "secret",
                        },
                    },
                )
                .then((res) => {
                    console.log(res);

                    console.log("Access Token:", res.data.access_token);
                    localStorage.setItem("token", res.data.access_token);
                    navigate("/");
                    window.location.reload();
                })
                .catch((err) => {
                    console.error("Ошибка получения токена:", err);
                });
        }
    }, [location]);

    return <div>Обработка авторизации...</div>;
}

export default OAuth2Callback;
