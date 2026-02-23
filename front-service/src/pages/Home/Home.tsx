import React, { useEffect } from "react";
import classes from "./Home.module.scss";
import ReferenceForm from "../../components/ReferenceForm/ReferenceForm";
import LeftBar from "../../components/LeftBar/LeftBar";
import TopBar from "../../components/TopBar/TopBar";
import ContentLayout from "../../components/ContentLayout/ContentLayout";
import { useDispatch, useSelector } from "react-redux";
import { RootState } from "../../app/store";
import { setAlert } from "../../slices/referenceValueSlice";
import Alert from "../../components/Alert/Alert";

export default function Home() {
    const message = useSelector((state: RootState) => state.reference.messages);
    const referenceValue = useSelector(
        (state: RootState) => state.reference.data,
    );

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
                                flexDirection: "row",
                                gap: "10px",
                                width: "100%",
                            }}
                        >
                            <ReferenceForm />
                        </div>
                    </ContentLayout>
                </div>
            </div>
        </>
    );
}
