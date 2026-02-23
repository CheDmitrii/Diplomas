import React, { useState } from "react";
import classes from "./Button.module.scss";

interface ButtonProps {
    children: React.ReactNode;
    color?: string;
    backgroundColor?: string;
    onClick?: () => void;
    disabled?: boolean;
    sx?: React.CSSProperties;
    type?: "button" | "reset" | "submit" | undefined;
}

export default function Button({
    children,
    color,
    sx,
    backgroundColor,
    onClick,
    type,
    disabled,
}: ButtonProps) {
    const [isPressed, setIsPressed] = useState(false);

    const style: React.CSSProperties = {
        ...sx,
        backgroundColor: backgroundColor,
        color: color,
        filter: isPressed ? "brightness(1)" : "",
    };

    const handleMouseDown = () => {
        setIsPressed(true);
    };

    const handleMouseUp = () => {
        setIsPressed(false);
    };

    const handleMouseLeave = () => {
        setIsPressed(false);
    };

    return (
        <button
            type={type}
            disabled={disabled}
            onClick={onClick}
            style={style}
            onMouseDown={handleMouseDown}
            onMouseUp={handleMouseUp}
            onMouseLeave={handleMouseLeave}
            className={classes.Button}
        >
            {children}
        </button>
    );
}
