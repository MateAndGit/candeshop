import React, { useState } from "react";
import LoginForm from "../components/auth/LoginForm";
import RegisterForm from "../components/auth/RegisterForm";
import styles from "../pages/Auth.module.css";

const AuthPage = () => {
  const [mode, setMode] = useState("login");

  const switchModeHandler = (newMode) => {
    setMode(newMode);
  };

  return (
    <div className={styles.wrapper}>
      <div className={styles.card}>
        <div className={styles.header}>
          <h1>Sunflower 🌻</h1>
          <p>너를 위한 따뜻한 공간</p>
        </div>

        {mode === "login" ? (
          <LoginForm onSwitchMode={() => switchModeHandler("signup")} />
        ) : (
          <RegisterForm onSwitchMode={() => switchModeHandler("login")} />
        )}
      </div>
    </div>
  );
};

export default AuthPage;
