import classes from "./OAuth2.module.scss";
import LeftBar from "../../components/LeftBar/LeftBar";
import TopBar from "../../components/TopBar/TopBar";
import ContentLayout from "../../components/ContentLayout/ContentLayout";
import Input from "../../components/UI/Input/Input";
import { ReactEventHandler, useRef, useState } from "react";
import Button from "../../components/UI/Button/Button";
import axios from "axios";

export default function OAuth2() {
    const [loginValue, setLoginValue] = useState("");
    const [passwordValue, setPasswordValue] = useState("");

    const loginHandler = (e: React.ChangeEvent<HTMLInputElement>) => {
        setLoginValue(e.target.value);
    };
    const passwordHandler = (e: React.ChangeEvent<HTMLInputElement>) => {
        setPasswordValue(e.target.value);
    };

    return (
        <>
            <div className={classes.App}>
                <LeftBar />
                <div className={classes.RightContainer}>
                    <TopBar />
                    <ContentLayout>
                        <div
                            style={{
                                display: "flex",
                                flexDirection: "column",
                                alignItems: "center",
                                width: "100%",
                            }}
                        >
                            <div className={classes.Wrapper}>
                                <h1>
                                    Для продолжения необходимо авторизоваться
                                </h1>
                                <form
                                    action="http://localhost:9000/login"
                                    method="POST"
                                >
                                    {/* Невидимые настоящие инпуты для отправки данных */}
                                    <input
                                        type="hidden"
                                        name="username"
                                        value={loginValue}
                                    />
                                    <input
                                        type="hidden"
                                        name="password"
                                        value={passwordValue}
                                    />

                                    {/* Кастомные инпуты для UI */}
                                    <Input
                                        value={loginValue}
                                        onChange={(e) =>
                                            setLoginValue(e.target.value)
                                        }
                                        placeholder="Ваш логин"
                                    />
                                    <Input
                                        value={passwordValue}
                                        onChange={(e) =>
                                            setPasswordValue(e.target.value)
                                        }
                                        placeholder="Ваш пароль"
                                    />

                                    <Button
                                        sx={{ width: "100%" }}
                                        type="submit" // или просто убрать onClick и сделать type=submit, а обработчик формы handleSubmit
                                    >
                                        Войти
                                    </Button>
                                </form>
                            </div>
                        </div>
                    </ContentLayout>
                </div>
            </div>
        </>
    );
}
