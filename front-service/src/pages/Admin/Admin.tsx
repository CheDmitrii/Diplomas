import {
    FormControl,
    InputLabel,
    MenuItem,
    Select,
    Checkbox,
    ListItemText,
    SelectChangeEvent,
} from "@mui/material";
import Alert from "../../components/Alert/Alert";
import ContentLayout from "../../components/ContentLayout/ContentLayout";
import LeftBar from "../../components/LeftBar/LeftBar";
import ReferenceForm from "../../components/ReferenceForm/ReferenceForm";
import TopBar from "../../components/TopBar/TopBar";
import Button from "../../components/UI/Button/Button";
import Input from "../../components/UI/Input/Input";
import classes from "./Admin.module.scss";
import { useSelector } from "react-redux";
import { RootState } from "../../app/store";
import { useState } from "react";
import axios from "axios";

export default function Admin() {
    const sensors = useSelector((state: RootState) => state.reference.data);
    const [formData, setFormData] = useState({
        sensorName: "",
        referenceValue: "",
        unit: "",
        username: "",
        password: "",
        firstname: "",
        lastname: "",
        selectedSensors: [] as string[], // добавлено состояние для выбранных датчиков
    });

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setFormData((prev) => ({
            ...prev,
            [name]: value,
        }));
    };

    const handleSensorChange = (
        event: SelectChangeEvent<string[]>, // Используем SelectChangeEvent для Select с множественным выбором
    ) => {
        const { value } = event.target; // Получаем value, которое является массивом строк

        // Проверяем, что value всегда массив строк
        const selectedSensors = Array.isArray(value) ? value : [value];

        // Обновляем выбранные датчики в state
        setFormData((prev) => ({
            ...prev,
            selectedSensors, // Прямо используем значение, оно всегда будет string[]
        }));
    };

    const createSensor = async () => {
        const token = localStorage.getItem("token");
        try {
            const response = await axios.post(
                "http://localhost:8228/sensor/create",
                {
                    name: formData.sensorName,
                    type: formData.unit,
                },
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                        "Content-Type": "application/json",
                    },
                },
            );

            console.log("Sensor created with ID:", response.data.id);
        } catch (error) {
            if (axios.isAxiosError(error)) {
                console.error(
                    "Error creating sensor:",
                    error.response?.data || error.message,
                );
            } else {
                console.error("Unknown error:", (error as Error).message);
            }
        }
    };
    console.log(formData.selectedSensors);

    const createUser = async () => {
        const token = localStorage.getItem("token"); // если нужна авторизация

        const newUser = {
            login: formData.username,
            password: formData.password,
            firstName: formData.firstname,
            lastName: formData.lastname,
            role: "8739f3fc-0403-4c85-9b90-1cbf5956bd1e",
            // добавляем выбранные датчики в запрос, если это нужно
            selectedSensors: formData.selectedSensors,
        };

        try {
            const response = await axios.post(
                "http://localhost:9000/admin/create-user",
                newUser,
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                        "Content-Type": "application/json",
                    },
                },
            );
            console.log("User created successfully", response.status);
        } catch (error) {
            if (axios.isAxiosError(error)) {
                console.error(
                    "Error creating user:",
                    error.response?.data || error.message,
                );
            } else {
                console.error("Unknown error:", (error as Error).message);
            }
        }
    };

    return (
        <>
            <div className={classes.Admin}>
                <LeftBar />
                <div className={classes.RightContainer}>
                    <TopBar />
                    <ContentLayout>
                        <div
                            style={{
                                display: "flex",
                                gap: "10px",
                                width: "100%",
                                height: "590px",
                                flexWrap: "wrap",
                                justifyContent: "center",
                            }}
                        >
                            <div
                                className={`${classes.SensorForm} + ${classes.Item}`}
                            >
                                <div className={classes.Wrapper}>
                                    <div className={classes.Container}>
                                        <h1>Добавление датчиков</h1>
                                        <div className={classes.InputItem}>
                                            <Input
                                                name="sensorName"
                                                value={formData.sensorName}
                                                placeholder="Название датчика"
                                                onChange={handleInputChange}
                                            ></Input>
                                        </div>
                                        <div className={classes.InputItem}>
                                            <Input
                                                name="referenceValue"
                                                value={formData.referenceValue}
                                                placeholder="Эталонное значение"
                                                onChange={handleInputChange}
                                            ></Input>
                                        </div>
                                        <div className={classes.InputItem}>
                                            <Input
                                                name="unit"
                                                value={formData.unit}
                                                placeholder="Единица измерения"
                                                onChange={handleInputChange}
                                            ></Input>
                                        </div>
                                        <Button onClick={createSensor}>
                                            Добавить датчик
                                        </Button>
                                    </div>
                                </div>
                            </div>
                            {/* <div
                                className={`${classes.SensorForm} + ${classes.Item}`}
                            >
                                <div className={classes.Wrapper}>
                                    <h1>Редактирование пользователя</h1>
                                </div>
                            </div> */}
                            <div
                                className={`${classes.SensorForm} + ${classes.Item}`}
                            >
                                <div className={classes.Wrapper}>
                                    <div
                                        className={classes.Container}
                                        style={{
                                            maxWidth: "500px",
                                        }}
                                    >
                                        <h1>Создать пользователя</h1>
                                        <div className={classes.InputItem}>
                                            <Input
                                                name="username"
                                                placeholder="Логин"
                                                value={formData.username}
                                                onChange={handleInputChange}
                                            ></Input>
                                        </div>
                                        <div className={classes.InputItem}>
                                            <Input
                                                name="password"
                                                placeholder="Пароль"
                                                value={formData.password}
                                                onChange={handleInputChange}
                                            ></Input>
                                        </div>
                                        <div className={classes.InputItem}>
                                            <Input
                                                name="firstname"
                                                placeholder="Имя"
                                                value={formData.firstname}
                                                onChange={handleInputChange}
                                            ></Input>
                                        </div>
                                        <div className={classes.InputItem}>
                                            <Input
                                                name="lastname"
                                                placeholder="Фамилия"
                                                value={formData.lastname}
                                                onChange={handleInputChange}
                                            ></Input>
                                        </div>
                                        <FormControl
                                            fullWidth
                                            sx={{
                                                "& .MuiOutlinedInput-root": {
                                                    backgroundColor: "#171717",
                                                    color: "white", // цвет текста внутри Select
                                                    textWrap: "wrap",
                                                    "& fieldset": {
                                                        borderColor: "#171717", // цвет границы
                                                        borderWidth: "2px",
                                                    },
                                                    "&:hover fieldset": {
                                                        borderColor: "#fff", // цвет границы при наведении
                                                    },
                                                    "&.Mui-focused fieldset": {
                                                        borderColor: "#fff", // цвет границы при фокусе
                                                    },
                                                },
                                                "& .MuiInputLabel-root": {
                                                    color: "grey", // цвет текста label
                                                },
                                                "& .MuiInputLabel-root.Mui-focused":
                                                    {
                                                        color: "lightgrey", // цвет label при фокусе
                                                    },
                                            }}
                                        >
                                            <InputLabel id="demo-simple-select-label">
                                                Разрешенные датчики
                                            </InputLabel>
                                            <Select
                                                labelId="demo-simple-select-label"
                                                id="demo-simple-select"
                                                multiple
                                                value={formData.selectedSensors}
                                                onChange={handleSensorChange}
                                                renderValue={(selected) => {
                                                    const selectedNames =
                                                        selected.map(
                                                            (id: string) => {
                                                                const sensor =
                                                                    sensors.find(
                                                                        (
                                                                            sensor,
                                                                        ) =>
                                                                            sensor.id ===
                                                                            id,
                                                                    );
                                                                return sensor
                                                                    ? sensor.name
                                                                    : "";
                                                            },
                                                        );
                                                    return selectedNames.join(
                                                        ", ",
                                                    );
                                                }}
                                            >
                                                {sensors.length !== 0
                                                    ? sensors.map((sensor) => (
                                                          <MenuItem
                                                              sx={{
                                                                  "& span": {
                                                                      color: "black !important",
                                                                  },
                                                              }}
                                                              key={sensor.id}
                                                              value={sensor.id}
                                                          >
                                                              <Checkbox
                                                                  checked={
                                                                      formData.selectedSensors.indexOf(
                                                                          sensor.id,
                                                                      ) > -1
                                                                  }
                                                              />
                                                              <ListItemText
                                                                  primary={
                                                                      sensor.name
                                                                  }
                                                              />
                                                          </MenuItem>
                                                      ))
                                                    : null}
                                            </Select>
                                        </FormControl>
                                        <Button onClick={createUser}>
                                            Создать пользователя
                                        </Button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </ContentLayout>
                </div>
            </div>
        </>
    );
}
