import React, { useState } from "react";
import classes from "./TopBar.module.scss";
import { useDispatch, useSelector } from "react-redux";
import type { RootState } from "../../app/store";
import { ReactComponent as AddTrans } from "./img/addTrans.svg";
import alertIcon from "./img/alertIcon.png";
import Button from "../UI/Button/Button";
import axios from "axios";
import { readAlerts } from "../../slices/referenceValueSlice";

function TopBar() {
    const [showSensors, setShowSensors] = useState(false);
    const [showAlerts, setShowAlerts] = useState(false);
    const [sensorData, setSensorData] = useState<
        Array<{
            name: string;
            id: string;
            status: "GOOD" | "BAD" | "NOT_ACTIVE";
        }>
    >([]);
    const alerts = useSelector((state: RootState) => state.reference.alerts);
    const unreadCount = useSelector(
        (state: RootState) => state.reference.unreadCount,
    );
    const dispatch = useDispatch();

    const handleAlerts = () => {
        setShowAlerts(!showAlerts);
        dispatch(readAlerts());
    };

    const checkSensors = async () => {
        const token = localStorage.getItem("token");

        try {
            const response = await axios.get(
                "http://localhost:8228/sensor/check-sensors",
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                        "Content-Type": "application/json",
                    },
                },
            );

            const sensors = response.data;
            console.log("Checked sensors:", sensors);
            setSensorData(response.data);
        } catch (error) {
            if (axios.isAxiosError(error)) {
                console.error(
                    "Error checking sensors:",
                    error.response?.data || error.message,
                );
            } else {
                console.error("Unknown error:", (error as Error).message);
            }
        }
        setShowSensors(true);
    };

    const isLight = false;
    const countOkSensors = () => {
        return sensorData.filter((sensor) => sensor.status === "GOOD").length;
    };

    return (
        <div className={classes.TopBar}>
            <span>Система мониторинга</span>
            <div className={classes.Sobaka}>
                <div style={{ position: "relative" }}>
                    <Button backgroundColor="black" onClick={checkSensors}>
                        Проверить датчики
                    </Button>

                    {showSensors && (
                        <div className={classes.SensorDropdown}>
                            <div className={classes.DropCont}>
                                <span>
                                    {countOkSensors()}/{sensorData.length} OK
                                </span>
                                <span>
                                    <u
                                        style={{
                                            cursor: "pointer",
                                        }}
                                        onClick={() => setShowSensors(false)}
                                    >
                                        свернуть
                                    </u>
                                </span>
                            </div>
                            {sensorData.map((sensor, index) => (
                                <div key={index} className={classes.SensorItem}>
                                    <strong>{sensor.name}:</strong>{" "}
                                    {sensor.status}
                                </div>
                            ))}
                        </div>
                    )}
                </div>

                {!isLight ? (
                    <div>
                        <a className={classes.Button} onClick={handleAlerts}>
                            <img src={alertIcon} alt="Alert" />
                            {unreadCount > 0 && (
                                <span className={classes.Badge}>
                                    {unreadCount}
                                </span>
                            )}
                        </a>
                        {showAlerts && (
                            <div className={classes.AlertDropdown}>
                                {alerts.length === 0 ? (
                                    <span>Нет уведомлений</span>
                                ) : (
                                    alerts.map((msg, index) => (
                                        <div
                                            key={index}
                                            className={classes.AlertItem}
                                        >
                                            <strong>{msg.id}</strong> — значение{" "}
                                            {msg.value}
                                            <br />
                                            <small>
                                                {new Date(
                                                    msg.time,
                                                ).toLocaleString()}
                                            </small>
                                        </div>
                                    ))
                                )}
                            </div>
                        )}
                    </div>
                ) : null}
            </div>
        </div>
    );
}

export default TopBar;
