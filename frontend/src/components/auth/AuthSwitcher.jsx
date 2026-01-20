import stylse from "../auth/AuthSwitcher.module.css";

export default function AuthSwitcher({ isLogin, onSwitchMode }) {
  return (
    <div className={stylse.switch_text}>
      {isLogin ? "계정이 없나요? " : "이미 계정이 있나요?"}
      <span onClick={onSwitchMode}>
        {isLogin ? "회원가입 하러가기" : "로그인 하러가기"}
      </span>
    </div>
  );
}
