import classes from "./Process.module.scss";
import LeftBar from "../../components/LeftBar/LeftBar";
import TopBar from "../../components/TopBar/TopBar";
import ContentLayout from "../../components/ContentLayout/ContentLayout";
import Alert from "../../components/Alert/Alert";
import { useSelector } from "react-redux";
import { RootState } from "../../app/store";
import techPic from "./img/techprocess.png";
import { useEffect, useState } from "react";

export default function Process() {
    const sensors = useSelector((state: RootState) => state.reference.data);
    const message = useSelector((state: RootState) => state.reference.messages);
    const [actualValues, setActualValues] = useState({
        first: "",
        second: "",
        third: "",
        fourth: "",
        fifth: "",
        sixth: "",
        seventh: "",
        eighth: "",
        nineth: "",
        tenth: "",
        eleventh: "",
        twelweth: "",
        thrirteenth: "",
    });

    useEffect(() => {
        if (message) {
            // Обновляем только конкретные значения в зависимости от id
            setActualValues((prevValues) => ({
                ...prevValues, // Сохраняем старые значения
                [getFieldNameById(message.id)]: message.value, // Обновляем нужное поле
            }));
        }
    }, [message]);

    // Функция для получения названия поля по id сообщения
    const getFieldNameById = (id: string) => {
        switch (id) {
            case "976b3741-2f02-48fc-988c-53d86e3338fe":
                return "first";
            case "1087010f-5f88-40a9-b7d0-dbaeb3ab3a2a":
                return "second";
            case "3c703a09-0889-44d6-949b-0996732470d2":
                return "third";
            case "36574620-1278-49cb-9dcf-fd2a34148aa2":
                return "fourth";
            case "59f6f71b-3ede-4c63-a162-fbc4dd53ffdd":
                return "fifth";
            case "63ef82f5-86ad-44be-80ec-1afe20d571d7":
                return "sixth";
            case "ea636933-a999-439c-9b0e-3ec36c129f75":
                return "seventh";
            case "a47cb5ac-af55-407b-81c5-107c96e95cdc":
                return "eighth";
            case "a2589d51-4039-49ed-8ec5-156596685c72":
                return "nineth";
            case "84dead43-305c-4e35-b6d2-61034fa7fdd9":
                return "tenth";
            case "dd991a28-bf2a-4fe6-907f-4bb5eda632f0":
                return "eleventh";
            case "0b7b42fa-a54e-4713-ba10-f09b059f800d":
                return "twelweth";
            case "8d526fcf-73bb-4a48-8f70-0a3a8d88de5b":
                return "thrirteenth";
            default:
                return "";
        }
    };

    return (
        <>
            <div className={classes.App}>
                <LeftBar />
                <div className={classes.RightContainer}>
                    <TopBar />
                    <ContentLayout>
                        <div className={classes.TechProcess}>
                            <h1
                                style={{
                                    textAlign: "center",
                                }}
                            >
                                Технологический процесс
                            </h1>
                            <img src={techPic} />
                            <span className={classes.First}>
                                {actualValues.first} мм
                            </span>
                            <span className={classes.Second}>
                                {actualValues.second} ℃
                            </span>
                            <span className={classes.Third}>
                                {actualValues.third} о/мин
                            </span>
                            <span className={classes.Fourth}>
                                {actualValues.fourth} МПа
                            </span>
                            <span className={classes.Fifth}>
                                {actualValues.fifth} ℃
                            </span>
                            <span className={classes.Sixth}>
                                {actualValues.sixth} ℃
                            </span>
                            <span className={classes.Seventh}>
                                {actualValues.seventh} мм
                            </span>
                            <span className={classes.Eighth}>
                                {actualValues.eighth} мм
                            </span>
                            <span className={classes.Nineth}>
                                {actualValues.nineth} мм
                            </span>
                            <span className={classes.Tenth}>
                                {actualValues.tenth} ℃
                            </span>
                            <span className={classes.Eleventh}>
                                {actualValues.eleventh} ℃
                            </span>
                            <span className={classes.Twelweth}>
                                {actualValues.twelweth} мм
                            </span>
                            <span className={classes.Thrirteenth}>
                                {actualValues.thrirteenth} л/мин
                            </span>
                        </div>
                    </ContentLayout>
                </div>
            </div>
        </>
    );
}
