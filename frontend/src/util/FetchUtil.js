export async function refreshAccessToken() {
  // 이제 localStorage에서 refreshToken을 읽어올 필요가 없습니다. (쿠키에 있으므로)

  const response = await fetch(
    `${import.meta.env.VITE_BACKEND_API_BASE_URL}/api/jwt/reissue`,
    {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      // 중요: 브라우저가 HttpOnly 쿠키(Refresh Token)를 자동으로 서버에 보내게 함
      credentials: "include",
    },
  );

  if (!response.ok) {
    // 갱신 실패 시 (리프레시 토큰 만료 등)
    throw new Error("AccessToken 갱신 실패");
  }

  const data = await response.json();

  // 새로 발급받은 AccessToken만 저장합니다.
  // 만약 서버에서 새로운 RefreshToken도 보냈다면, 서버가 Set-Cookie로 처리해줄 것입니다.
  localStorage.setItem("accessToken", data.accessToken);

  return data.accessToken;
}

export async function fetchWithAccess(url, options = {}) {
  let accessToken = localStorage.getItem("accessToken");
  console.log("🚀 [요청 시작] URL:", url);

  if (!options.headers) options.headers = {};
  options.headers["Authorization"] = `Bearer ${accessToken}`;
  options.credentials = "include";

  let response = await fetch(url, options);

  if (response.status === 401) {
    console.warn("⚠️ [401 발생] Access Token 만료됨. 갱신을 시도합니다...");

    try {
      accessToken = await refreshAccessToken();
      console.log("✅ [갱신 성공] 새로운 Access Token으로 재요청을 보냅니다.");

      options.headers["Authorization"] = `Bearer ${accessToken}`;
      response = await fetch(url, options);
      console.log("🎉 [재요청 결과] 성공!");
    } catch (err) {
      console.error(
        "❌ [갱신 실패] Refresh Token도 만료되었거나 오류가 발생했습니다.",
      );
      localStorage.removeItem("accessToken");
      window.location.href = "/";
      return Promise.reject("인증 만료");
    }
  }

  return response;
}
