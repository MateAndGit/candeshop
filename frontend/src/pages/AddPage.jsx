import styles from "./EditPage.module.css"; // 이미 만들어둔 CSS 재사용
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { fetchWithAccess } from "../util/FetchUtil"; // 경로 및 파일명 반영

const BACKEND_API_BASE_URL = import.meta.env.VITE_BACKEND_API_BASE_URL;

export default function AddPage() {
  const navigate = useNavigate();

  // 상태 관리
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [price, setPrice] = useState("");
  const [error, setError] = useState("");

  const handleAdd = async (e) => {
    e.preventDefault();
    setError("");

    // 1. 유효성 검사
    if (!title.trim() || !description.trim() || price <= 0) {
      setError("모든 필드를 올바르게 입력해주세요.");
      return;
    }

    try {
      const response = await fetchWithAccess(
        `${BACKEND_API_BASE_URL}/api/products`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            title,
            description,
            price: Number(price),
          }),
        },
      );

      // 3. 결과 처리
      if (response.ok) {
        alert("상품이 성공적으로 등록되었습니다! 🌻");
        navigate("/main"); // 등록 후 메인 페이지로 이동
      } else if (response.status === 403) {
        //에서 발생한 403 에러 대응
        setError("등록 권한이 없습니다. 관리자 계정인지 확인해주세요.");
      } else {
        throw new Error("등록 중 서버 오류가 발생했습니다.");
      }
    } catch (err) {
      console.error("등록 실패:", err);
      setError(err.message);
    }
  };

  return (
    <div className={styles.edit_container}>
      <h2>새 상품 등록 🌻</h2>

      {error && <p className={styles.error_msg}>{error}</p>}

      <form onSubmit={handleAdd} className={styles.edit_form}>
        <div className={styles.input_group}>
          <label>상품명</label>
          <input
            type="text"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            placeholder="예: 태양을 닮은 해바라기"
            required
          />
        </div>

        <div className={styles.input_group}>
          <label>상품 설명</label>
          <textarea
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            placeholder="상품에 대한 상세 설명을 입력하세요"
            required
          />
        </div>

        <div className={styles.input_group}>
          <label>가격</label>
          <input
            type="number"
            value={price}
            onChange={(e) => setPrice(e.target.value)}
            placeholder="0"
            required
          />
        </div>

        <div className={styles.btn_group}>
          <button
            type="button"
            className={styles.cancel_btn}
            onClick={() => navigate(-1)}
          >
            취소
          </button>
          <button type="submit" className={styles.submit_btn}>
            상품 등록하기
          </button>
        </div>
      </form>
    </div>
  );
}
