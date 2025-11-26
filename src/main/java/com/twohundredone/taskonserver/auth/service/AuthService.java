package com.twohundredone.taskonserver.auth.service;

import static com.twohundredone.taskonserver.global.enums.ResponseStatusError.EMAIL_ALREADY_EXISTS;
import static com.twohundredone.taskonserver.global.enums.ResponseStatusError.INVALID_REFRESH_TOKEN;
import static com.twohundredone.taskonserver.global.enums.ResponseStatusError.PASSWORD_INCORRECT;
import static com.twohundredone.taskonserver.global.enums.ResponseStatusError.PASSWORD_INCORRECT_MISMATCH;
import static com.twohundredone.taskonserver.global.enums.ResponseStatusError.REFRESH_TOKEN_NOT_FOUND;
import static com.twohundredone.taskonserver.global.enums.ResponseStatusError.USER_NOT_FOUND;

import com.twohundredone.taskonserver.auth.dto.LoginRequest;
import com.twohundredone.taskonserver.auth.dto.LoginResponse;
import com.twohundredone.taskonserver.auth.dto.ReissueResponse;
import com.twohundredone.taskonserver.auth.dto.SignUpRequest;
import com.twohundredone.taskonserver.auth.dto.SignUpResponse;
import com.twohundredone.taskonserver.auth.jwt.JwtProvider;
import com.twohundredone.taskonserver.auth.util.CookieUtil;
import com.twohundredone.taskonserver.global.exception.CustomException;
import com.twohundredone.taskonserver.user.entity.User;
import com.twohundredone.taskonserver.user.repository.UserRepository;
import com.twohundredone.taskonserver.user.service.OnlineStatusService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    private final OnlineStatusService onlineStatusService;

    @Transactional
    public SignUpResponse signUp(SignUpRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new CustomException(EMAIL_ALREADY_EXISTS);
        }

        if (!request.password().equals(request.passwordCheck())){
            throw new CustomException(PASSWORD_INCORRECT_MISMATCH);
        }

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .name(request.name())
                .build();

        userRepository.save(user);

        return SignUpResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .name(user.getName())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }

    public boolean checkEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new CustomException(EMAIL_ALREADY_EXISTS);
        }
        return true;
    }

    @Transactional
    public LoginResponse  login(LoginRequest request, HttpServletResponse response) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new CustomException(PASSWORD_INCORRECT);
        }

        String accessToken = jwtProvider.createAccessToken(user.getUserId(), user.getEmail());
        String refreshToken = jwtProvider.createRefreshToken(user.getUserId(), user.getEmail());

        // Redis에 RefreshToken 저장
        refreshTokenService.save(
                user.getUserId(),
                refreshToken,
                jwtProvider.getRefreshTokenValidity() // TTL(ms)
        );

        // RefreshToken → Cookie 저장
        CookieUtil.addRefreshTokenCookie(response, refreshToken);

        onlineStatusService.setOnline(user.getUserId());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .user(LoginResponse.UserInfo.builder()
                        .userId(user.getUserId())
                        .email(user.getEmail())
                        .name(user.getName())
                        .profileImageUrl(user.getProfileImageUrl())
                        .build())
                .build();
    }

    public ReissueResponse reissue(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = CookieUtil.getRefreshTokenFromCookie(request);
        if (refreshToken == null) {
            throw new CustomException(REFRESH_TOKEN_NOT_FOUND);
        }

        // RefreshToken 유효성 검증
        jwtProvider.validateToken(refreshToken);

        Long userId = jwtProvider.getUserId(refreshToken);

        // Redis에서 RefreshToken 조회
        String storedToken = refreshTokenService.get(userId);
        if (storedToken == null || !storedToken.equals(refreshToken)) {
            throw new CustomException(INVALID_REFRESH_TOKEN);
        }

        // AccessToken과 RefreshToken 재발급
        String email = jwtProvider.getEmail(refreshToken);
        String newAccess = jwtProvider.createAccessToken(userId, email);
        String newRefresh = jwtProvider.createRefreshToken(userId, email);

        // Redis에 RefreshToken 갱신 저장 (TTL 그대로 유지)
        refreshTokenService.save(
                userId,
                newRefresh,
                jwtProvider.getRefreshTokenValidity()
        );

        // 쿠키 재설정
        CookieUtil.addRefreshTokenCookie(response, newRefresh);

        return new ReissueResponse(newAccess);
    }

    // 로그아웃(RefreshToken 제거)
    public void logout(Long userId, HttpServletResponse response)  {
        // 1) Refresh Token 삭제 (Redis)
        refreshTokenService.delete(userId);

        // 2) Refresh Token Cookie 삭제
        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader("Set-Cookie", deleteCookie.toString());
        onlineStatusService.setOffline(userId);
    }
}
