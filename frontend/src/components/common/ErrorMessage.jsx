import styles from "../common/ErrorMessage.module.css";

export default function ErrorMessage({ message }) {
  if (!message) return null;
  return (
    <div className={styles.errorContainer}>
      <div className={styles.errorText}>
        <p>⚠️ {message}</p>
      </div>
    </div>
  );
}
