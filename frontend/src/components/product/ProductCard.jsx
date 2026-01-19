import styles from "./ProductCard.module.css";
import { fetchWithAccess } from "../../util/FetchUtil";
import { useNavigate } from "react-router-dom";

const BACKEND_API_BASE_URL = import.meta.env.VITE_BACKEND_API_BASE_URL;

export default function ProductCard({ product, isAdmin }) {
  const { id, title, description, price } = product;
  const navigate = useNavigate();

  // 수정/삭제 핸들러
  const handleEdit = (e) => {
    e.stopPropagation();
    console.log(`수정 ID: ${id}`);
    navigate(`/product/edit/${id}`);
  };

  const handleDelete = async (e) => {
    e.stopPropagation();
    if (!window.confirm("이 상품을 정말 삭제할까요?")) return;
    try {
      const response = await fetchWithAccess(
        `${import.meta.env.VITE_BACKEND_API_BASE_URL}/api/products/${id}`,
        { method: "DELETE" },
      );

      if (response.ok) {
        alert("삭제되었습니다.");
        window.location.reload();
      } else {
        alert("삭제 권한이 없거나 오류가 발생했습니다.");
      }
    } catch (err) {
      console.error("삭제 실패:", err);
    }
  };

  const handleAddToCart = async () => {
    try {
      const response = await fetchWithAccess(
        `${BACKEND_API_BASE_URL}/api/cart/add`,
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ productId: id, amount: 1 }),
        },
      );

      if (response.ok) {
        alert("장바구니에 담겼습니다! 🌻");
        window.location.reload();
      } else {
        const errorMsg = await response.text();
        alert(errorMsg);
      }
    } catch (err) {
      console.error("장바구니 담기 실패:", err);
    }
  };

  return (
    <div className={styles.product_card}>
      <div className={styles.img_box}>
        {/* 관리자일 때만 이미지 위에 U, D 버튼 노출 */}
        {isAdmin && (
          <>
            <button
              className={`${styles.admin_btn} ${styles.update}`}
              onClick={handleEdit}
            >
              U
            </button>
            <button
              className={`${styles.admin_btn} ${styles.delete}`}
              onClick={handleDelete}
            >
              D
            </button>
          </>
        )}
        🌻
      </div>

      <div className={styles.info}>
        <h4>{title}</h4>
        <h5>{description}</h5>
        <p className={styles.price}>{price.toLocaleString()}원</p>
        <button className={styles.cart_btn} onClick={handleAddToCart}>
          담기
        </button>
      </div>
    </div>
  );
}
