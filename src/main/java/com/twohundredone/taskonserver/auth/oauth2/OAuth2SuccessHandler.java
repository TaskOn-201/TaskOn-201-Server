package com.twohundredone.taskonserver.auth.oauth2;

import com.twohundredone.taskonserver.auth.jwt.JwtProvider;
import com.twohundredone.taskonserver.auth.util.CookieUtil;
import com.twohundredone.taskonserver.user.entity.User;
import com.twohundredone.taskonserver.user.service.OnlineStatusService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final JwtProvider jwtProvider;
    private final OnlineStatusService onlineStatusService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        CustomOAuth2User oAuthUser = (CustomOAuth2User) authentication.getPrincipal();
        User user = oAuthUser.getUser();

        String accessToken = jwtProvider.createAccessToken(user.getUserId(), user.getEmail());
        String refreshToken = jwtProvider.createRefreshToken(user.getUserId(), user.getEmail());

        CookieUtil.addRefreshTokenCookie(response, refreshToken);

        onlineStatusService.setOnline(user.getUserId());

        String redirectUrl = "http://localhost:3000/oauth2/success?accessToken=" + accessToken;

        try {
            response.sendRedirect(redirectUrl);
        } catch (IllegalStateException e) {
            log.error("Redirect failed, response already committed", e);
        }
    }
}
