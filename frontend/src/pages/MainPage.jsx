import styles from "./MainPage.module.css";
import Navbar from "../components/common/Navbar";
import { useEffect, useState } from "react";
import Loading from "../components/common/Loading";
import ProductCard from "../components/product/ProductCard";
import ErrorMessage from "../components/common/ErrorMessage";
import { fetchWithAccess } from "../util/FetchUtil";
import { getUserInfo } from "../util/api";

const BACKEND_API_BASE_URL = import.meta.env.VITE_BACKEND_API_BASE_URL;

export default function MainPage() {
  const [products, setProducts] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState("");

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const userInfo = getUserInfo();
  const isAdmin = userInfo?.role === "ROLE_ADMIN";

  useEffect(() => {
    const fetchProducts = async () => {
      try {
        setIsLoading(true);
        const token = localStorage.getItem("accessToken");

        const response = await fetchWithAccess(
          `${BACKEND_API_BASE_URL}/api/products?page=${page}`,
          {
            method: "GET",
            headers: {
              "Content-Type": "application/json",
              Authorization: token ? `Bearer ${token}` : "",
            },
          },
        );

        if (!response.ok) throw new Error("상품 불러오기 실패");

        const data = await response.json();
        setProducts(data.content); // 실제 상품 리스트
        setTotalPages(data.totalPages); // 전체 페이지 수 (버튼 생성용)
      } catch (err) {
        setError(
          err === "인증 만료"
            ? "세션이 만료되어 로그인 페이지로 이동합니다."
            : err.message,
        );
      } finally {
        setIsLoading(false);
      }
    };

    fetchProducts();
  }, [page]);

  return (
    <div className={styles.main_container}>
      <div className={styles.wrapper}>
        <Navbar />
        <div className={styles.banner}>
          <h2>Summer Collection 🌻</h2>
          <p>가장 따뜻한 마음을 선물하세요</p>
        </div>
        <br />
        <h3 className={styles.section_title}>추천 상품</h3>
        {/* 관리자일 때만 등록 버튼 표시 */}
        {isAdmin && (
          <button
            className={styles.admin_add_btn}
            onClick={() => (window.location.href = "/product/new")}
          >
            상품 등록
          </button>
        )}
        {isLoading ? (
          <Loading isLoading={isLoading} />
        ) : (
          <>
            <div className={styles.product_grid}>
              {products.map((product) => (
                <ProductCard
                  key={product.id}
                  product={product}
                  isAdmin={isAdmin}
                />
              ))}
            </div>

            <div className={styles.pagination}>
              <button
                disabled={page === 0}
                onClick={() => setPage((prev) => prev - 1)}
                className={styles.pageBtn}
              >
                &lt;
              </button>

              {Array.from({ length: totalPages }, (_, i) => (
                <button
                  key={i}
                  className={page === i ? styles.activePage : styles.pageBtn}
                  onClick={() => {
                    setPage(i);
                    window.scrollTo({ top: 0, behavior: "smooth" });
                  }}
                >
                  {i + 1}
                </button>
              ))}
              <button
                disabled={page >= totalPages - 1}
                onClick={() => setPage((prev) => prev + 1)}
                className={styles.pageBtn}
              >
                &gt;
              </button>
            </div>
          </>
        )}
        <ErrorMessage message={error} />
      </div>
    </div>
  );
}
