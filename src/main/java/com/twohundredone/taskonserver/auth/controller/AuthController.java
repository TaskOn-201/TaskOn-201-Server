package com.twohundredone.taskonserver.auth.controller;

import com.twohundredone.taskonserver.auth.dto.LoginRequest;
import com.twohundredone.taskonserver.auth.dto.SignUpRequest;
import com.twohundredone.taskonserver.auth.dto.TokenPair;
import com.twohundredone.taskonserver.auth.dto.LoginResponse;
import com.twohundredone.taskonserver.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody @Valid SignUpRequest request) {
        authService.signUp(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // AccessToken → Body
    // RefreshToken → HttpOnly Cookie
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        TokenPair tokens = authService.login(request);

        // RefreshToken을 HttpOnly 쿠키에 저장
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", tokens.refreshToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(60 * 60 * 24 * 14) // 14일
                .build();

        response.addHeader("Set-Cookie", refreshCookie.toString());

        // AccessToken만 반환
        return ResponseEntity.ok(new LoginResponse(tokens.accessToken()));
    }


    // AccessToken 재발급
    @PostMapping("/reissue")
    public ResponseEntity<LoginResponse> reissue(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        TokenPair tokens = authService.reissue(request, response);
        return ResponseEntity.ok(new LoginResponse(tokens.accessToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal Long userId, HttpServletResponse response) {

        authService.logout(userId);

        // RefreshToken 쿠키 삭제
        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader("Set-Cookie", deleteCookie.toString());

        return ResponseEntity.ok().build();
    }
}
