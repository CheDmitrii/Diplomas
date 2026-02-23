import { createSlice } from "@reduxjs/toolkit";
import { Journal } from "../components/Graph/Graph";

interface ReferenceValueState {
    data: ReferenceValueInterface[];
    messages: Journal;
    alerts: Array<{ id: string; value: string; time: string }>;
    unreadCount: number;
}

interface ReferenceValueInterface {
    id: string;
    reference: { value: string; id: string };
    name: string;
    journal: Journal;
}

const initialState: ReferenceValueState = {
    data: [],
    alerts: [],
    unreadCount: 0,
    messages: {
        time: "",
    },
};

const referenceValueSlice = createSlice({
    name: "referenceValue",
    initialState,
    reducers: {
        setReferenceValues: (state, action) => {
            state.data = action.payload;
        },
        setMessages: (state, action) => {
            const { id, value, time } = action.payload;
            // Обновляем или добавляем новое сообщение в объект messages с ключом id
            state.messages.id = id;
            state.messages.value = value;
            state.messages.time = time;
        },
        setAlert: (state, action) => {
            if (!action.payload.id) return;
            state.alerts.push({
                id: action.payload.id,
                value: action.payload.value,
                time: action.payload.time,
            });
            state.unreadCount += 1;
        },
        readAlerts: (state) => {
            state.unreadCount = 0;
        },
    },
});

export const { setReferenceValues, setMessages, setAlert, readAlerts } =
    referenceValueSlice.actions;
export default referenceValueSlice.reducer;
