export function getUserInfo() {
  const token = localStorage.getItem("accessToken");
  if (!token) return null;

  try {
    const base64Url = token.split(".")[1];
    const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
    const payload = JSON.parse(window.atob(base64));

    return {
      username: payload.username,
      role: payload.role,
    };
  } catch (error) {
    return null;
  }
}
