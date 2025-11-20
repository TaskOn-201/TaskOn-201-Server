// OAuth2 Provider 호출
function oauthLogin(provider) {
  const url = `http://localhost:8080/oauth2/authorization/${provider}`;
  window.location.href = url;
}

// 페이지 접속 시 URL에 accessToken 있는지 확인
window.onload = function () {
  const params = new URLSearchParams(window.location.search);
  const token = params.get("accessToken");

  if (token) {
    document.getElementById("token").innerText = token;
  }
};
