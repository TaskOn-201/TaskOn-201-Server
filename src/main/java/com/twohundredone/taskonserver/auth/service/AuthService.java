package com.twohundredone.taskonserver.auth.service;

import com.twohundredone.taskonserver.auth.dto.LoginRequest;
import com.twohundredone.taskonserver.auth.dto.SignUpRequest;
import com.twohundredone.taskonserver.auth.dto.TokenPair;
import com.twohundredone.taskonserver.auth.jwt.JwtProvider;
import com.twohundredone.taskonserver.user.entity.User;
import com.twohundredone.taskonserver.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public void signUp(SignUpRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("DUPLICATE_EMAIL");
        }

        if (!request.password().equals(request.passwordCheck())){
            throw new IllegalArgumentException("PASSWORD_INCORRECT_MISMATCH");
        }

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .name(request.name())
                .build();

        userRepository.save(user);
    }

    public boolean checkEmail(String email) {
        return !userRepository.existsByEmail(email);
    }

    public TokenPair login(LoginRequest request) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                request.email(),
                request.password()
        );

        // TODO: 로그인 된 사용자인지 boolean값 response해주도록 코드 수정

        User user = userRepository.findByEmail(request.email()).orElseThrow(
                () -> new IllegalArgumentException("USER_NOT_FOUND")
        );

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("PASSWORD_INCORRECT");
        }

        Authentication authenticated = authenticationManager.authenticate(authentication);
        CustomUserDetails principal = (CustomUserDetails) authenticated.getPrincipal();

        String accessToken = jwtProvider.createAccessToken(principal.getId(), principal.getUsername());
        String refreshToken = jwtProvider.createRefreshToken(principal.getId(), principal.getUsername());

        // Redis에 RefreshToken 저장
        refreshTokenService.save(principal.getId(), refreshToken, jwtProvider.getRefreshTokenValidityMs());

        return new TokenPair(accessToken, refreshToken);
    }

    public TokenPair reissue(HttpServletRequest request, HttpServletResponse response) {

        // 쿠키에서 RefreshToken 가져오기
        String refreshToken = extractRefreshToken(request);
        if (refreshToken == null)
            throw new IllegalArgumentException("NO_REFRESH_TOKEN_IN_COOKIE");

        // 토큰 파싱 (유효성 + 만료 여부 확인)
        var claims = jwtProvider.parseToken(refreshToken).getBody();
        Long userId = Long.valueOf(claims.getSubject());
        String email = claims.get("email", String.class);

        // Redis 저장 RefreshToken과 비교
        String saved = refreshTokenService.get(userId);
        if (saved == null || !saved.equals(refreshToken)) {
            throw new IllegalArgumentException("INVALID_REFRESH_TOKEN");
        }

        // 유저 검증
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("USER_NOT_FOUND"));

        // AccessToken 재발급
        String newAccessToken = jwtProvider.createAccessToken(user.getUserId(), user.getEmail());

        // RefreshToken Rotation(재발급) — 기업형 보안
        String newRefreshToken = jwtProvider.createRefreshToken(user.getUserId(), user.getEmail());
        refreshTokenService.save(user.getUserId(), newRefreshToken, jwtProvider.getRefreshTokenValidityMs());

        // 새 RefreshToken을 쿠키에 저장
        ResponseCookie cookie = ResponseCookie.from("refreshToken", newRefreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(jwtProvider.getRefreshTokenValidityMs() / 1000)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());

        return new TokenPair(newAccessToken, newRefreshToken);
    }

    private String extractRefreshToken(HttpServletRequest request) {
        if (request.getCookies() == null)
            return null;
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals("refreshToken")) {
                return cookie.getValue();
            }
        }
        return null;
    }

    // 로그아웃(RefreshToken 제거)
    public void logout(Long userId) {
        refreshTokenService.delete(userId);
    }
}
