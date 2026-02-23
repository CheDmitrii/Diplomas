import classes from "./Auth.module.scss";
import LeftBar from "../../components/LeftBar/LeftBar";
import TopBar from "../../components/TopBar/TopBar";
import ContentLayout from "../../components/ContentLayout/ContentLayout";
import Input from "../../components/UI/Input/Input";
import { ReactEventHandler, useState } from "react";
import Button from "../../components/UI/Button/Button";
import axios from "axios";

export default function Auth() {
    const token = localStorage.getItem("token");

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
                            {!token ? (
                                <div className={classes.Wrapper}>
                                    <h1>
                                        Для продолжения необходимо
                                        авторизоваться
                                    </h1>
                                    <a
                                        style={{
                                            width: "100%",
                                            textAlign: "center",
                                        }}
                                        href="http://localhost:9000/oauth2/authorize?response_type=code&client_id=client&scope=openid&redirect_uri=http://localhost:3000/oauth2/callback"
                                    >
                                        <Button
                                            sx={{
                                                width: "100%",
                                            }}
                                        >
                                            Продолжить
                                        </Button>
                                    </a>
                                </div>
                            ) : (
                                <div className={classes.Wrapper}>
                                    <h1>Вы уже авторизованы</h1>
                                    <form
                                        action="http://localhost:9000/logout"
                                        method="POST"
                                    >
                                        <Button
                                            onClick={() => localStorage.clear()}
                                        >
                                            Выйти
                                        </Button>
                                    </form>
                                </div>
                            )}
                        </div>
                    </ContentLayout>
                </div>
            </div>
        </>
    );
}
