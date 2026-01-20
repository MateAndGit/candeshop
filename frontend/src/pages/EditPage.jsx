import styles from "./EditPage.module.css";
import { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { fetchWithAccess } from "../util/FetchUtil";

const BACKEND_API_BASE_URL = import.meta.env.VITE_BACKEND_API_BASE_URL;

export default function EditPage() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [price, setPrice] = useState("");
  const [error, setError] = useState("");

  // 1. 기존 상품 정보 불러오기
  useEffect(() => {
    const fetchProduct = async () => {
      try {
        const response = await fetchWithAccess(
          `${BACKEND_API_BASE_URL}/api/products/${id}`,
        );
        if (!response.ok) throw new Error("상품 정보를 불러오지 못했습니다.");

        const data = await response.json();
        setTitle(data.title);
        setDescription(data.description);
        setPrice(data.price);
      } catch (err) {
        setError(err.message);
      }
    };
    fetchProduct();
  }, [id]);

  // 2. 수정 요청 보내기
  const handleEdit = async (e) => {
    e.preventDefault();
    setError("");

    if (!title.trim() || !description.trim() || price < 0) {
      setError("모든 필드를 올바르게 입력해주세요.");
      return;
    }

    try {
      const token = localStorage.getItem("accessToken");

      const response = await fetchWithAccess(
        `${BACKEND_API_BASE_URL}/api/products/edit/${id}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            Authorization: token ? `Bearer ${token}` : "",
          },
          body: JSON.stringify({ title, description, price }),
        },
      );

      if (response.ok) {
        alert("성공적으로 수정되었습니다.");
        navigate("/main");
      } else {
        throw new Error("수정에 실패했습니다.");
      }
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div className={styles.edit_container}>
      <h2>상품 정보 수정 🌻</h2>
      {error && <p className={styles.error_msg}>{error}</p>}

      <form onSubmit={handleEdit} className={styles.edit_form}>
        <input
          type="text"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          placeholder="상품명"
        />
        <textarea
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          placeholder="상품 설명"
        />
        <input
          type="number"
          value={price}
          onChange={(e) => setPrice(e.target.value)}
          placeholder="가격"
        />
        <div className={styles.btn_group}>
          <button type="button" onClick={() => navigate(-1)}>
            취소
          </button>
          <button type="submit" className={styles.submit_btn}>
            수정 완료
          </button>
        </div>
      </form>
    </div>
  );
}
