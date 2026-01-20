import { useEffect, useState } from "react";
import styles from "./Navbar.module.css";
import { useNavigate } from "react-router-dom";
import { fetchWithAccess } from "../../util/FetchUtil";
import { getUserInfo } from "../../util/api";

const BACKEND_API_BASE_URL = import.meta.env.VITE_BACKEND_API_BASE_URL;

export default function Navbar() {
  const [cartCount, setCartCount] = useState(0);
  const [userInfo, setUserInfo] = useState(null);
  const navigate = useNavigate();

  const handleCartClick = () => {
    navigate("/cart");
  };

  const handleLogout = () => {
    localStorage.removeItem("accessToken");
    navigate("/");
  };

  useEffect(() => {
    const user = getUserInfo();
    setUserInfo(user);
    const fetchCartNum = async () => {
      try {
        const response = await fetchWithAccess(
          `${BACKEND_API_BASE_URL}/api/cart`,
          { method: "GET" },
        );

        if (!response.ok) throw new Error("장바구니 정보 불러오기 실패");

        const data = await response.json();
        setCartCount(data.totalCount || 0);
      } catch (err) {
        if (import.meta.env.DEV) {
          console.error("Cart fetch error:", err.message);
        }
      }
    };

    fetchCartNum();
  }, []);

  return (
    <nav className={styles.nav}>
      <div className={styles.nav_left}>
        <span
          className={styles.logo}
          onClick={() => navigate("/main")}
          style={{ cursor: "pointer" }}
        >
          🌻 SunShop
        </span>
      </div>
      <div className={styles.nav_right}>
        <span>
          {userInfo ? `${userInfo.username.split("@")[0]}님` : "Guest님"}
        </span>
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
