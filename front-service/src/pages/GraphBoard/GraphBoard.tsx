import React, { useEffect, useState } from "react";
import classes from "./GraphBoard.module.scss";
import Graph from "../../components/Graph/Graph";
import LeftBar from "../../components/LeftBar/LeftBar";
import TopBar from "../../components/TopBar/TopBar";
import ContentLayout from "../../components/ContentLayout/ContentLayout";
import { useDispatch, useSelector } from "react-redux";
import { RootState } from "../../app/store";
import CurrentValues from "../../components/CurrentValues/CurrentValues";
import { setAlert } from "../../slices/referenceValueSlice";
import Alert from "../../components/Alert/Alert";
import { TextField } from "@mui/material";

export default function GraphBoard() {
    const referenceValue = useSelector(
        (state: RootState) => state.reference.data,
    );
    const alerts = useSelector((state: RootState) => state.reference.alerts);
    const dispatch = useDispatch();
    const message = useSelector((state: RootState) => state.reference.messages);

    const [search, setSearch] = useState("");

    console.log(referenceValue);

    useEffect(() => {
        console.log("worked");

        if (message) {
            const refValues = referenceValue.reduce<{
                [key: string]: { val: string; name: string };
            }>((acc, item) => {
                // Проверяем, есть ли поле reference и reference.value
                if (item.reference && item.reference.value) {
                    acc[item.id] = {
                        val: item.reference.value,
                        name: item.name,
                    };
                } else {
                    acc[item.id] = { val: "Нет данных", name: item.name }; // Значение по умолчанию
                }
                return acc;
            }, {});

            console.log(refValues);

            const date = new Date(message.time);

            // Преобразуем в строку с нужным форматом
            const formattedDate = date.toLocaleString();

            // Если пришедшее сообщение не совпадает с текущим значением, показываем alert
            if (refValues[message.id]?.val !== message.value) {
                dispatch(
                    setAlert({
                        id: message.id,
                        value: message.value,
                        time: formattedDate,
                    }),
                );
            }
        }
    }, [message, referenceValue, dispatch]);

    // Фильтруем графики по поисковому запросу
    const filteredGraphs = referenceValue.filter((item) =>
        item.name.toLowerCase().includes(search.toLowerCase()),
    );

    return (
        <>
            <div className={classes.App}>
                <LeftBar />
                <div className={classes.RightContainer}>
                    <TopBar />
                    <ContentLayout>
                        <div style={{ padding: "1rem", width: "100%" }}>
                            <TextField
                                fullWidth
                                label="Поиск по названию графика"
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
                                        <div
                                            key={index}
                                            className={classes.Item}
                                        >
                                            <Graph
                                                name={item.name}
                                                id={item.id}
                                                reference={
                                                    item.reference?.value ||
                                                    "Нет данных"
                                                } // Проверка на наличие reference
                                            />
                                        </div>
                                    ))
                                ) : (
                                    <div>Нет совпадений</div>
                                )}
                            </div>
                        </div>
                    </ContentLayout>
                </div>
            </div>
        </>
    );
}
