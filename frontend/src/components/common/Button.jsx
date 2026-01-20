import styles from "../common/Button.module.css";

export default function Button({ type, name }) {
  return (
    <button className={styles.button} type={type}>
      {name}
    </button>
  );
}
