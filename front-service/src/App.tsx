import classes from "./App.module.scss";
import LeftBar from "./components/LeftBar/LeftBar";
import TopBar from "./components/TopBar/TopBar";
import { useDispatch, useSelector } from "react-redux";
import type { RootState } from "./app/store";
import ContentLayout from "./components/ContentLayout/ContentLayout";
import { Client } from "@stomp/stompjs";
import { useEffect, useState } from "react";
import SockJS from "sockjs-client";
import axios from "axios";
import ReferenceForm from "./components/ReferenceForm/ReferenceForm";
import Graph, { Journal } from "./components/Graph/Graph";
import Button from "./components/UI/Button/Button";
import { setMessages, setReferenceValues } from "./slices/referenceValueSlice";
import {
    BrowserRouter,
    Navigate,
    Route,
    Routes,
    useNavigate,
} from "react-router";
import Home from "./pages/Home/Home";
import GraphBoard from "./pages/GraphBoard/GraphBoard";
import Admin from "./pages/Admin/Admin";
import Auth from "./pages/Auth/Auth";
import OAuth2 from "./pages/OAuth2/OAuth2";
import OAuth2Callback from "./pages/OAuth2Callback/OAuth2Callback";
import Process from "./pages/Process/Process";
import PrivateRoute from "./components/PrivateRoute/PrivateRoute";
// import OAuth2Callback from "./pages/OAuth2Callback/OAuth2Callback";

function App() {
    const [client, setClient] = useState<Client | null>(null);
    const dispatch = useDispatch();
    const token = localStorage.getItem("token") ?? "";
    const sensors = useSelector((state: RootState) => state.reference.data);
    const alerts = useSelector((state: RootState) => state.reference.alerts);

    console.log("alerts", alerts);

    useEffect(() => {
        const socket = new WebSocket(
            `ws://localhost:8228/socket?token=${token}`,
        );

        socket.onopen = () => {
            console.log("WebSocket connected");

            // Подписка на топики для каждого датчика
            sensors.forEach((sensor) => {
                const subscribeMessage = {
                    action: "subscribe", // Тип сообщения для подписки
                    topic: `/topic/journal/${sensor.id}`, // ID датчика - это топик
                };

                socket.send(JSON.stringify(subscribeMessage));
                console.log("Subscription message sent", subscribeMessage);
            });
        };

        socket.onmessage = (event) => {
            const msg = JSON.parse(event.data);
            console.log("Received:", msg);
            const date = new Date(msg.time);

            // Преобразуем в строку с нужным форматом
            const formattedDate = date.toLocaleString();
            dispatch(
                setMessages({
                    id: msg.id,
                    value: msg.value,
                    time: formattedDate,
                }),
            );
        };

        socket.onerror = (error) => {
            console.error("WebSocket error:", error);
        };

        socket.onclose = () => {
            console.log("WebSocket closed");
        };

        return () => {
            socket.close();
        };
    }, [token, sensors]);

    useEffect(() => {
        axios
            .get("http://localhost:8228/sensor/all-sensors", {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            })
            .then((response) => {
                dispatch(setReferenceValues(response.data));
            })
            .catch((err) => {
                console.error("Ошибка при загрузке данных:", err);
            });
    }, []);

    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<Navigate to="/home" replace />} />
                <Route
                    path="/home"
                    element={
                        <PrivateRoute>
                            <Home />
                        </PrivateRoute>
                    }
                />
                <Route
                    path="/graph"
                    element={
                        <PrivateRoute>
                            <GraphBoard />
                        </PrivateRoute>
                    }
                />
                <Route
                    path="/admin"
                    element={
                        <PrivateRoute>
                            <Admin />
                        </PrivateRoute>
                    }
                />
                <Route path="/login" element={<OAuth2 />} />
                <Route path="/auth" element={<Auth />} />
                <Route path="/oauth2/callback" element={<OAuth2Callback />} />
                <Route
                    path="/process"
                    element={
                        <PrivateRoute>
                            <Process />
                        </PrivateRoute>
                    }
                />
            </Routes>
        </BrowserRouter>
    );
}

export default App;
