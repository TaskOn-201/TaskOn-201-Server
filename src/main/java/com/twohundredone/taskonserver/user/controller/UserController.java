package com.twohundredone.taskonserver.user.controller;

import static com.twohundredone.taskonserver.global.enums.ResponseStatusSuccess.MODIFY_USER_INFO_SUCCESS;
import static com.twohundredone.taskonserver.global.enums.ResponseStatusSuccess.UPDATE_PASSWORD_SUCCESS;

import com.twohundredone.taskonserver.auth.service.CustomUserDetails;
import com.twohundredone.taskonserver.global.dto.ApiResponse;
import com.twohundredone.taskonserver.user.dto.UserPasswordUpdateRequest;
import com.twohundredone.taskonserver.user.dto.UserProfileResponse;
import com.twohundredone.taskonserver.user.dto.UserProfileUpdateRequest;
import com.twohundredone.taskonserver.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "사용자 정보 변경", description = "사용자 정보 변경 API")
    @SecurityRequirement(name = "Authorization")
    @PatchMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            @Valid @RequestPart(value = "request") UserProfileUpdateRequest request
    ) {
        UserProfileResponse response = userService.updateProfile(userDetails.getId(), request, profileImage);
        return ResponseEntity.ok(
            ApiResponse.success(MODIFY_USER_INFO_SUCCESS, response)
        );
    }

    @Operation(summary = "비밀번호 변경", description = "비밀번호 변경 API")
    @SecurityRequirement(name = "Authorization")
    @PatchMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> updatePasword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody UserPasswordUpdateRequest request
    ) {
        userService.updatePassword(userDetails.getId(), request);
        return ResponseEntity.ok(
                ApiResponse.success(UPDATE_PASSWORD_SUCCESS, null)
        );
    }
}
