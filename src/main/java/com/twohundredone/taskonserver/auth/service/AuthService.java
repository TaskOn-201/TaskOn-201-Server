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

        // TODO: 여기서 refreshToken을 Redis에 저장할 예정 (다음 단계)
        // refreshTokenService.save(principal.getId(), refreshToken);

        return new TokenResponse(accessToken, refreshToken);
    }
}
