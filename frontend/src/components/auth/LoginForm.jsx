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

  const naviagate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    console.log("로그인 시도:", { email, password });

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

      const data = await res.json();
      localStorage.setItem("accessToken", data.accessToken);
      naviagate("/profile");
      if (!res.ok) throw new Error("로그인 실패");
    } catch {
      setError("로그인 중 오류가 발생했습니다.");
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
        <Button type="submint" name="로그인 하기" />
      </form>
      <AuthSwitcher isLogin={true} onSwitchMode={onSwitchMode} />
      <br />
      {<ErrorMessage message={error} />}
    </div>
  );
};

export default LoginForm;
