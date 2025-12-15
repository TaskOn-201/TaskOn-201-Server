package com.twohundredone.taskonserver.auth.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;

public class CookieUtil {
    public static final String REFRESH_TOKEN_COOKIE = "refreshToken";

    // RefreshToken 쿠키 저장
    public static void addRefreshTokenCookie(
            HttpServletRequest request,
            HttpServletResponse response,
            String refreshToken
    ) {
        boolean isLocal = request.getServerName().contains("localhost");

        ResponseCookie.ResponseCookieBuilder cookieBuilder = ResponseCookie
                .from(REFRESH_TOKEN_COOKIE, refreshToken)
                .httpOnly(true)
                .path("/")
                .maxAge(60 * 60 * 24 * 14);

        if (isLocal) {
            // 🔥 로컬테스트용
            cookieBuilder
                    .secure(false)
                    .sameSite("Lax") // localhost에서는 None 불가
                    .domain(null);
        } else {
            // 🔥 실제 배포(api.taskon.co.kr)
            cookieBuilder
                    .secure(true)
                    .sameSite("None")
                    .domain(".taskon.co.kr"); // 모든 서브도메인 허용
        }

        response.addHeader("Set-Cookie", cookieBuilder.build().toString());
    }

    // RefreshToken 쿠키 읽기
    public static String getRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;

        for (Cookie cookie : request.getCookies()) {
            if (REFRESH_TOKEN_COOKIE.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    public static void deleteRefreshTokenCookie(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        boolean isLocal = request.getServerName().contains("localhost");

        ResponseCookie.ResponseCookieBuilder cookieBuilder =
                ResponseCookie.from(REFRESH_TOKEN_COOKIE, "")
                        .httpOnly(true)
                        .path("/")
                        .maxAge(0);

        if (isLocal) {
            cookieBuilder.secure(false).sameSite("Lax").domain(null);
        } else {
            cookieBuilder.secure(true).sameSite("None").domain(".taskon.co.kr");
        }

        response.addHeader("Set-Cookie", cookieBuilder.build().toString());
    }

}
