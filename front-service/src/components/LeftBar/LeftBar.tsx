import React from "react";
import classes from "./LeftBar.module.scss";
import logoIcon from "./img/logo.png";
import graphIcon from "./img/graph.png";
import adminIcon from "./img/admin.png";
import logoIconLight from "./img/dashLight.png";
import graphIconLight from "./img/graphLight.png";
import adminIconLight from "./img/adminLight.png";
import bellLight from "./img/bellLight.png";
import loginIcon from "./img/loginIcon.png";
import techIcon from "./img/tech.png";

function LeftBar() {
    const isLight = false;
    const lightStyleBar = {
        justifyContent: "space-between",
    };
    const lightStyleIcons = {
        display: "flex",
        flexDirection: "column",
        width: "100%",
        alignItems: "center",
    };
    return (
        <div className={classes.LeftBar} style={lightStyleBar}>
            {isLight ? (
                <a className={classes.Button} href="/home">
                    <img src={bellLight} className={classes.PngIcon} />
                </a>
            ) : null}
            <div
                style={{
                    display: "flex",
                    flexDirection: "column",
                    width: "100%",
                    alignItems: "center",
                }}
            >
                <a className={classes.Button} href="/home">
                    <img
                        src={isLight ? logoIconLight : logoIcon}
                        className={classes.PngIcon}
                    />
                </a>
                <a className={classes.Button} href="/graph">
                    <img
                        src={isLight ? graphIconLight : graphIcon}
                        style={{
                            width: "35px",
                            height: "35px",
                        }}
                        className={classes.PngIcon}
                    />
                </a>
                <a className={classes.Button} href="/admin">
                    <img
                        src={isLight ? adminIconLight : adminIcon}
                        className={classes.PngIcon}
                    />
                </a>
                <a className={classes.Button} href="/process">
                    <img src={techIcon} className={classes.PngIcon} />
                </a>
            </div>
            <a className={classes.Button} href="/auth">
                <img src={loginIcon} className={classes.PngIcon} />
            </a>
        </div>
    );
}

export default LeftBar;
