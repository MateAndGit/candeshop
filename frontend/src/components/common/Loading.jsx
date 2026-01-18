import styles from "./Loading.module.css";

export default function Loading({ isLoading }) {
  if (!isLoading) return null; // 로딩 중이 아니면 아무것도 안 보여줌

  return (
    <div className={styles.loading}>
      <div>⌛</div>
      Loading...
    </div>
  );
}
