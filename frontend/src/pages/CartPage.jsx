import { useEffect, useState } from "react";
import styles from "./CartPage.module.css";
import { fetchWithAccess } from "../util/FetchUtil";
import { useNavigate } from "react-router-dom";

const BACKEND_API_BASE_URL = import.meta.env.VITE_BACKEND_API_BASE_URL;

export default function CartPage() {
  const [cartData, setCartData] = useState(null);
  const [loading, setLoading] = useState(true);

  const navigate = useNavigate();

  // 1. Obtener datos del carrito
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
          console.error("Error al cargar el carrito:", err);
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCart();
  }, []);

  // 2. Manejador de eliminación de items
  const handleDelete = async (itemId) => {
    if (!window.confirm("¿Estás seguro de que quieres eliminar este artículo?")) return;
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
        console.error("Error al eliminar:", err);
      }
    }
  };

  const handleOrder = async () => {
    if (!window.confirm("¿Estás seguro de que quieres realizar el pedido?")) return;

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
        alert("¡Pedido realizado con éxito! Revisa tu correo electrónico 💌");
        navigate("/main");
      } else {
        const errorDetail = await response.text();
        if (import.meta.env.DEV) {
          console.error("서버 응답 에러:", errorDetail);
        }
        alert("Error al procesar el pedido. Por favor, revisa los logs del servidor.");
      }
    } catch (err) {
      if (import.meta.env.DEV) {
        console.error("통신 에러:", err);
      }
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <div className={styles.loading}>Cargando... 🌻</div>;

  return (
    <div className={styles.container}>
      <h2>Mi Carrito 🛒</h2>

      {cartData?.cartItem.content.length > 0 ? (
        <>
          <div className={styles.cart_list}>
            {cartData.cartItem.content.map((item) => (
              <div key={item.cartItemId} className={styles.cart_item}>
                <div className={styles.item_info}>
                  <h4>{item.title}</h4>
                  <p>${item.price.toLocaleString()}</p>
                </div>
                <button
                  className={styles.delete_btn}
                  onClick={() => handleDelete(item.cartItemId)}
                >
                  Eliminar
                </button>
              </div>
            ))}
          </div>

          <div className={styles.summary}>
            <h3>총 결제 금액: {cartData.totalCartPrice.toLocaleString()}원</h3>
            <p>Total de productos: {cartData.totalCount}</p>
            <div className={styles.button_group}>
              <button className={styles.order_btn} onClick={handleOrder}>
                Realizar Pedido
              </button>
              <button
                className={`${styles.order_btn} ${styles.back_btn}`}
                onClick={() => navigate(-1)}
              >
                Volver
              </button>
            </div>
          </div>
        </>
      ) : (
        <p className={styles.empty_msg}>¡Tu carrito está vacío! 🌻</p>
      )}
    </div>
  );
}
