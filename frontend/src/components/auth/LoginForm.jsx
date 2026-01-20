import React, { useState } from "react";
import Button from "../common/Button";
import Input from "../common/Input";
import AuthSwitcher from "./AuthSwitcher";
import ErrorMessage from "../common/ErrorMessage";
import { useNavigate } from "react-router-dom";

const BACKEND_API_BASE_URL = import.meta.env.VITE_BACKEND_API_BASE_URL;

const LoginForm = ({ onSwitchMode }) => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();

    if (email.trim() === "" || password.trim() === "") {
      setError("입력값을 다시 확인해주세요.");
      return;
    }

    try {
      const res = await fetch(`${BACKEND_API_BASE_URL}/api/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({ email, password }),
      });

      if (!res.ok) throw new Error("Error en el inicio de sesión");

      const data = await res.json();
      localStorage.setItem("accessToken", data.accessToken);
      navigate("/main");
    } catch {
      setError("Ha ocurrido un error durante el inicio de sesión.");
    }
  };

  return (
    <div className="form-box">
      <form onSubmit={handleLogin}>
        <Input
          type="email"
          placeholder="이메일"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />
        <Input
          type="password"
          placeholder="비밀번호"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />
        <Button type="submit" name="Iniciar Sesión" />
      </form>
      <AuthSwitcher isLogin={true} onSwitchMode={onSwitchMode} />
      <br />
      {<ErrorMessage message={error} />}
    </div>
  );
};

export default LoginForm;
