import { useEffect, useRef, useState } from "react";
import Input from "../UI/Input/Input";
import classes from "./ReferenceForm.module.scss";
import Button from "../UI/Button/Button";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";
import axios from "axios";
import ReferenceValue from "./ReferenceValue/ReferenceValue";
import { useSelector } from "react-redux";
import { RootState } from "../../app/store";
import { TextField } from "@mui/material";

export default function ReferenceForm() {
    const socketRef = useRef<WebSocket | null>(null); // 👈 создаем ref
    const referenceValues = useSelector(
        (state: RootState) => state.reference.data,
    );
    const [stompClient, setStompClient] = useState<Client | null>(null);
    const [inputValues, setInputValues] = useState<{ [id: string]: string }>(
        {},
    );
    const [updateSent, setUpdateSent] = useState<boolean>(false);

    useEffect(() => {
        const token = localStorage.getItem("token");
        const socket = new WebSocket(
            `ws://localhost:8228/socket?token=${token}`,
        );

        socketRef.current = socket;

        socket.onmessage = (event) => {
            const msg = JSON.parse(event.data);
            console.log("Update received:", msg);
        };
    }, []);

    const sendUpdate = (oldValue: string, newValue: string, id: string) => {
        if (
            socketRef.current &&
            socketRef.current.readyState === WebSocket.OPEN
        ) {
            const message = {
                type: "reference/update",
                data: {
                    id,
                    oldValue,
                    newValue,
                },
            };
            socketRef.current.send(JSON.stringify(message));
            console.log("Sent:", message);
        } else {
            console.warn("WebSocket is not connected.");
        }
    };

    const [search, setSearch] = useState("");

    const filteredGraphs = referenceValues.filter((item) =>
        item.name.toLowerCase().includes(search.toLowerCase()),
    );

    return (
        <div className={classes.ReferenceForm}>
            <h1>Эталонные значения</h1>
            <div className={classes.Container}>
                <TextField
                    fullWidth
                    label="Поиск по названию датчика"
                    variant="outlined"
                    value={search}
                    onChange={(e) => setSearch(e.target.value)}
                    sx={{
                        input: {
                            color: "white",
                        },
                        marginBottom: "1rem",
                        "& .MuiFormLabel-root": {
                            color: "white",
                        },
                        "& .MuiInputBase-root": {
                            borderRadius: "12px",
                            background: "#2a2929",
                            "& fieldset": {
                                borderRadius: "12px",
                            },
                            "&:hover fieldset": {
                                borderColor: "white",
                            },
                            "&.Mui-focused fieldset": {
                                borderColor: "white",
                            },
                        },
                    }}
                />
                <div
                    style={{
                        display: "flex",
                        gap: "10px",
                        flexWrap: "wrap",
                        height: "100%",
                        maxHeight: "300px",
                    }}
                >
                    {filteredGraphs.length > 0 ? (
                        filteredGraphs.map((item, index) => (
                            <div key={index} className={classes.RefContainer}>
                                <ReferenceValue
                                    isSent={updateSent}
                                    name={item.name}
                                    value={
                                        inputValues[item.id] ??
                                        item.reference?.value
                                    }
                                    onChange={(newValue) =>
                                        setInputValues((prev) => ({
                                            ...prev,
                                            [item.id]: newValue,
                                        }))
                                    }
                                />
                                <Button
                                    sx={{
                                        maxWidth: "none",
                                    }}
                                    onClick={() =>
                                        sendUpdate(
                                            String(item.reference.value),
                                            inputValues[item.id] ??
                                                item.reference.value,
                                            item.reference.id,
                                        )
                                    }
                                >
                                    Изменить
                                </Button>
                            </div>
                        ))
                    ) : (
                        <div>Нет совпадений</div>
                    )}
                </div>
                {/* {referenceValues[0] !== undefined
                    ? referenceValues.map((item, index) => {
                          console.log(item);

                          return (
                              <div key={index} className={classes.RefContainer}>
                                  <ReferenceValue
                                      isSent={updateSent}
                                      name={item.name}
                                      value={
                                          inputValues[item.id] ??
                                          item.reference.value
                                      }
                                      onChange={(newValue) =>
                                          setInputValues((prev) => ({
                                              ...prev,
                                              [item.id]: newValue,
                                          }))
                                      }
                                  />
                                  <Button
                                      sx={{
                                          maxWidth: "none",
                                      }}
                                      onClick={() =>
                                          sendUpdate(
                                              item.reference.value,
                                              inputValues[item.id] ??
                                                  item.reference.value,
                                              item.reference.id,
                                          )
                                      }
                                  >
                                      Изменить
                                  </Button>
                              </div>
                          );
                      })
                    : null} */}
            </div>
        </div>
    );
}
