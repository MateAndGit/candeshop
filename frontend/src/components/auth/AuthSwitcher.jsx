import stylse from "../auth/AuthSwitcher.module.css";

export default function AuthSwitcher({ isLogin, onSwitchMode }) {
  return (
    <div className={stylse.switch_text}>
      {isLogin ? "계정이 없나요? " : "이미 계정이 있나요?"}
      <span onClick={onSwitchMode}>
        {isLogin ? "Ir al registro" : "Ir al inicio de sesión"}
      </span>
    </div>
  );
}
