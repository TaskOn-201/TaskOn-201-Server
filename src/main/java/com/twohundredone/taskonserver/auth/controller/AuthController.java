package com.twohundredone.taskonserver.auth.controller;

import static com.twohundredone.taskonserver.global.enums.ResponseStatusSuccess.EMAIL_AVAILABLE;
import static com.twohundredone.taskonserver.global.enums.ResponseStatusSuccess.SIGNUP_SUCCESS;
import static com.twohundredone.taskonserver.global.enums.ResponseStatusSuccess.SUCCESS_LOGOUT;
import static com.twohundredone.taskonserver.global.enums.ResponseStatusSuccess.TOKEN_REISSUE_SUCCESS;

import com.twohundredone.taskonserver.auth.dto.EmailCheckResponse;
import com.twohundredone.taskonserver.auth.dto.LoginRequest;
import com.twohundredone.taskonserver.auth.dto.ReissueResponse;
import com.twohundredone.taskonserver.auth.dto.SignUpRequest;
import com.twohundredone.taskonserver.auth.dto.SignUpResponse;
import com.twohundredone.taskonserver.auth.dto.TokenPair;
import com.twohundredone.taskonserver.auth.dto.LoginResponse;
import com.twohundredone.taskonserver.auth.service.AuthService;
import com.twohundredone.taskonserver.auth.service.CustomUserDetails;
import com.twohundredone.taskonserver.global.dto.ApiResponse;
import com.twohundredone.taskonserver.global.enums.ResponseStatusSuccess;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class    AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입", description = "회원가입 API")
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignUpResponse>> signup(@Valid @RequestBody SignUpRequest request) {
        SignUpResponse response = authService.signUp(request);
        return ResponseEntity.ok(
                ApiResponse.success(SIGNUP_SUCCESS, response)
        );
    }

    @Operation(summary = "이메일 중복 확인", description = "회원가입 시 이메일 중복 확인 API")
    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse<EmailCheckResponse>> checkEmail(@RequestParam String email) {
        boolean isValid = authService.checkEmail(email);
        return ResponseEntity.ok(
                ApiResponse.success(EMAIL_AVAILABLE, new EmailCheckResponse(isValid))
        );
    }

    // AccessToken → Body
    // RefreshToken → HttpOnly Cookie
    @Operation(summary = "로그인", description = "로그인 API - AccessToken → Body / RefreshToken → HttpOnly Cookie")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        LoginResponse loginResponse = authService.login(request, response);

        return ResponseEntity.ok(
                ApiResponse.success(ResponseStatusSuccess.LOGIN_SUCCESS, loginResponse)
        );
    }


    @Operation(
            summary = "토큰 재발급",
            security = {
                    @SecurityRequirement(name = "RefreshTokenCookie")
            }
    )
    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<ReissueResponse>> reissue(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        ReissueResponse token = authService.reissue(request, response);

        return ResponseEntity.ok(
                ApiResponse.success(TOKEN_REISSUE_SUCCESS, token)
        );
    }

    @Operation(summary = "로그아웃", description = "JWT 로그아웃 API")
    @SecurityRequirement(name = "Authorization")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpServletResponse response
    ) {
        authService.logout(userDetails.getId(), response);

        return ResponseEntity.ok(
                ApiResponse.success(SUCCESS_LOGOUT, null)
        );
    }
}
