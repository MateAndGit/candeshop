import { useEffect, useState } from "react";
import styles from "./CartPage.module.css";
import { fetchWithAccess } from "../util/FetchUtil";
import { useNavigate } from "react-router-dom";

const BACKEND_API_BASE_URL = import.meta.env.VITE_BACKEND_API_BASE_URL;

export default function CartPage() {
  const [cartData, setCartData] = useState(null);
  const [loading, setLoading] = useState(true);

  const navigate = useNavigate();

  // 1. 장바구니 데이터 가져오기
  const fetchCart = async () => {
    try {
      const response = await fetchWithAccess(
        `${BACKEND_API_BASE_URL}/api/cart`,
        {
          method: "GET",
        },
      );
      if (response.ok) {
        const data = await response.json();
        setCartData(data);
      }
    } catch (err) {
      if (import.meta.env.DEV) {
        console.error("장바구니 로딩 실패:", err);
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCart();
  }, []);

  // 2. 아이템 삭제 핸들러
  const handleDelete = async (itemId) => {
    if (!window.confirm("삭제하시겠습니까?")) return;
    try {
      const response = await fetchWithAccess(
        `${BACKEND_API_BASE_URL}/api/cart/${itemId}`,
        {
          method: "DELETE",
        },
      );
      if (response.ok) {
        fetchCart(); // 삭제 후 목록 새로고침
      }
    } catch (err) {
      if (import.meta.env.DEV) {
        console.error("삭제 중 오류:", err);
      }
    }
  };

  const handleOrder = async () => {
    if (!window.confirm("정말로 주문하시겠습니까?")) return;

    const orderItems = cartData.cartItem.content.map((item) => ({
      productId: Number(item.productId),
      count: Number(item.amount),
    }));

    const orderRequest = {
      orderItems: orderItems,
      totalPrice: Number(cartData.totalCartPrice),
      totalCount: Number(cartData.totalCount),
    };

    try {
      setLoading(true);
      const response = await fetchWithAccess(
        `${BACKEND_API_BASE_URL}/api/orders/create`,
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(orderRequest),
        },
      );

      if (response.ok) {
        alert("주문 성공! 네이버 메일함을 확인하세요! 💌");
        navigate("/main");
      } else {
        const errorDetail = await response.text();
        if (import.meta.env.DEV) {
          console.error("서버 응답 에러:", errorDetail);
        }
        alert("주문 실패! 서버 로그를 확인해주세요.");
      }
    } catch (err) {
      if (import.meta.env.DEV) {
        console.error("통신 에러:", err);
      }
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <div className={styles.loading}>로딩 중... 🌻</div>;

  return (
    <div className={styles.container}>
      <h2>내 장바구니 🛒</h2>

      {cartData?.cartItem.content.length > 0 ? (
        <>
          <div className={styles.cart_list}>
            {cartData.cartItem.content.map((item) => (
              <div key={item.cartItemId} className={styles.cart_item}>
                <div className={styles.item_info}>
                  <h4>{item.title}</h4>
                  <p>{item.price.toLocaleString()}원</p>
                </div>
                <button
                  className={styles.delete_btn}
                  onClick={() => handleDelete(item.cartItemId)}
                >
                  삭제
                </button>
              </div>
            ))}
          </div>

          <div className={styles.summary}>
            <h3>총 결제 금액: {cartData.totalCartPrice.toLocaleString()}원</h3>
            <p>총 상품 개수: {cartData.totalCount}개</p>
            <div className={styles.button_group}>
              <button className={styles.order_btn} onClick={handleOrder}>
                주문하기
              </button>
              <button
                className={`${styles.order_btn} ${styles.back_btn}`}
                onClick={() => navigate(-1)}
              >
                뒤로가기
              </button>
            </div>
          </div>
        </>
      ) : (
        <p className={styles.empty_msg}>장바구니가 비어있어요! 🌻</p>
      )}
    </div>
  );
}
