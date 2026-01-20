import React, { useState } from "react";
import Button from "../common/Button";
import Input from "../common/Input";
import AuthSwitcher from "./AuthSwitcher";
import { useNavigate } from "react-router-dom";
import ErrorMessage from "../common/ErrorMessage";
import Swal from "sweetalert2";

const BACKEND_API_BASE_URL = import.meta.env.VITE_BACKEND_API_BASE_URL;

const RegisterForm = ({ onSwitchMode }) => {
  const [email, setEmail] = useState("");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  const navigate = useNavigate();

  const handleSignup = async (e) => {
    e.preventDefault();
    setError("");
    if (
      email.trim() === "" ||
      username.trim() === "" ||
      password.trim() === ""
    ) {
      setError("입력값을 다시 확인해주세요.");
      return;
    }

    try {
      const res = await fetch(`${BACKEND_API_BASE_URL}/api/users/join`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({ email, username, password }),
      });

      if (res.ok) {
        Swal.fire({
          title: "가입 완료!",
          text: "해바라기 마을의 주민이 되신 것을 환영합니다.",
          icon: "success",
          confirmButtonColor: "#FFC107",
        }).then(() => {
          navigate("/");
        });
      } else {
        throw new Error("회원가입 실패");
      }
    } catch {
      setError("회원가입 중 오류가 발생했습니다.");
    }
  };

  return (
    <div className="form-box">
      <form onSubmit={handleSignup}>
        <Input
          type="email"
          placeholder="이메일"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />
        <Input
          type="text"
          placeholder="이름"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
        />
        <Input
          type="password"
          placeholder="비밀번호"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />
        <Button type="submit" name="가입하고 꽃피우기" />
      </form>
      <AuthSwitcher isLogin={false} onSwitchMode={onSwitchMode} />
      <br />
      {<ErrorMessage message={error} />}
    </div>
  );
};

export default RegisterForm;
