import { useState } from "react";
import styles from "./Navbar.module.css";
import { useNavigate } from "react-router-dom";

export default function Navbar() {
  const [cartCount, setCartCount] = useState(3);
  const navigate = useNavigate();

  const handleCartClick = () => {
    alert("장바구니가 비어있어요!");
  };

  const handleLogout = () => {
    console.log("Logout clicked");
    navigate("/");
  };

  return (
    <nav className={styles.nav}>
      <div className={styles.nav_left}>
        <span className={styles.logo}>🌻 SunShop</span>
      </div>
      <div className={styles.nav_right}>
        <span>Guest님</span>
        {/* 장바구니 아이콘과 뱃지를 감싸는 컨테이너 */}
        <div className={styles.cart_wrapper}>
          <i
            className={`fas fa-shopping-cart ${styles.cart_icon}`}
            onClick={handleCartClick}
          ></i>
          {cartCount > 0 && <span className={styles.badge}>{cartCount}</span>}
        </div>
        <button className={styles.logout_btn} onClick={handleLogout}>
          로그아웃
        </button>
      </div>
    </nav>
  );
}
