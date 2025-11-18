package com.twohundredone.taskonserver.auth.service;

import com.twohundredone.taskonserver.auth.dto.LoginRequest;
import com.twohundredone.taskonserver.auth.dto.SignUpRequest;
import com.twohundredone.taskonserver.auth.dto.TokenResponse;
import com.twohundredone.taskonserver.auth.jwt.JwtProvider;
import com.twohundredone.taskonserver.user.entity.User;
import com.twohundredone.taskonserver.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .name(request.name())
                .build();

        userRepository.save(user);
    }

    public TokenResponse login(LoginRequest request) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                request.email(),
                request.password()
        );

        Authentication authenticated = authenticationManager.authenticate(authentication);
        CustomUserDetails principal = (CustomUserDetails) authenticated.getPrincipal();

        String accessToken = jwtProvider.createAccessToken(principal.getId(), principal.getUsername());
        String refreshToken = jwtProvider.createRefreshToken(principal.getId(), principal.getUsername());

        // Redis에 RefreshToken 저장
        refreshTokenService.save(principal.getId(), refreshToken, jwtProvider.getRefreshTokenValidityMs());

        return new TokenResponse(accessToken, refreshToken);
    }

    // RefreshToken으로 AccessToken 재발급 (필요시 Refresh도 재발급)
    public TokenResponse reissue(String refreshToken) {
        // 1. 토큰 파싱 & 유효성 검증
        var claims = jwtProvider.parseToken(refreshToken).getBody();
        Long userId = Long.valueOf(claims.getSubject());
        String email = claims.get("email", String.class);

        // 2. Redis에서 저장된 RefreshToken 확인
        String stored = refreshTokenService.get(userId);
        if (stored == null || !stored.equals(refreshToken)) {
            throw new IllegalArgumentException("INVALID_REFRESH_TOKEN");
        }

        // 3. 유저 정보 확인 (탈퇴/정지 등 체크용)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("USER_NOT_FOUND"));

        // 4. 새 AccessToken 발급
        String newAccessToken = jwtProvider.createAccessToken(user.getUserId(), user.getEmail());

        // (선택) RefreshToken도 재발급하고 싶으면 아래처럼:
        // String newRefreshToken = jwtProvider.createRefreshToken(user.getId(), user.getEmail());
        // refreshTokenService.save(user.getId(), newRefreshToken, jwtProvider.getRefreshTokenValidityMs());
        // return new TokenResponse(newAccessToken, newRefreshToken);

        // 지금은 RefreshToken 재사용: 기존거 내려줌
        return new TokenResponse(newAccessToken, refreshToken);
    }

    // 로그아웃(RefreshToken 제거)
    public void logout(Long userId) {
        refreshTokenService.delete(userId);
    }
}
