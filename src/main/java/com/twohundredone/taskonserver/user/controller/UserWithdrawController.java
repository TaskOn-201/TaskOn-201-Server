package com.twohundredone.taskonserver.user.controller;

import static com.twohundredone.taskonserver.global.enums.ResponseStatusSuccess.MODIFY_MY_INFO_SUCCESS;
import static com.twohundredone.taskonserver.global.enums.ResponseStatusSuccess.SUCCESS_DELETE_USER;

import com.twohundredone.taskonserver.auth.service.CustomUserDetails;
import com.twohundredone.taskonserver.global.dto.ApiResponse;
import com.twohundredone.taskonserver.user.service.UserWithdrawService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserWithdrawController {

    private final UserWithdrawService userWithdrawService;

    @Operation(summary = "회원탈퇴", description = "회원탈퇴 API")
    @SecurityRequirement(name = "Authorization")
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> withdraw(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        userWithdrawService.withdraw(userDetails.getId());
        return ResponseEntity.ok(
                ApiResponse.success(SUCCESS_DELETE_USER, null)
        );
    }
}
