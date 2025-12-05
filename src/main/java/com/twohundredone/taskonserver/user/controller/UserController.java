package com.twohundredone.taskonserver.user.controller;

import static com.twohundredone.taskonserver.global.enums.ResponseStatusSuccess.GET_MY_INFO;
import static com.twohundredone.taskonserver.global.enums.ResponseStatusSuccess.MODIFY_MY_INFO_SUCCESS;
import static com.twohundredone.taskonserver.global.enums.ResponseStatusSuccess.SELECTED_USER_SUCCESS;
import static com.twohundredone.taskonserver.global.enums.ResponseStatusSuccess.UPDATE_PASSWORD_SUCCESS;

import com.twohundredone.taskonserver.auth.service.CustomUserDetails;
import com.twohundredone.taskonserver.global.dto.ApiResponse;
import com.twohundredone.taskonserver.user.dto.UserMeResponse;
import com.twohundredone.taskonserver.user.dto.UserPasswordUpdateRequest;
import com.twohundredone.taskonserver.user.dto.UserProfileResponse;
import com.twohundredone.taskonserver.user.dto.UserProfileUpdateRequest;
import com.twohundredone.taskonserver.user.dto.UserSearchResponse;
import com.twohundredone.taskonserver.user.service.UserSearchService;
import com.twohundredone.taskonserver.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserSearchService userSearchService;

    @Operation(summary = "내 정보 변경", description = "내 정보 변경 API")
    @SecurityRequirement(name = "Authorization")
    @PatchMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @ModelAttribute UserProfileUpdateRequest request,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage
    ) {
        UserProfileResponse response = userService.updateProfile(userDetails.getId(), request, profileImage);
        return ResponseEntity.ok(
            ApiResponse.success(MODIFY_MY_INFO_SUCCESS, response)
        );
    }

    @Operation(summary = "내 정보 조회", description = "내 정보 조회 API")
    @SecurityRequirement(name = "Authorization")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserMeResponse>> getMyInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UserMeResponse response = userService.getMyInfo(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(GET_MY_INFO, response));
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

    @Operation(summary = "선택된 사용자 조회", description = "사용자가 검색 후 선택한 리스트를 조회한다.")
    @SecurityRequirement(name = "Authorization")
    @GetMapping("/selected")
    public ResponseEntity<ApiResponse<List<UserSearchResponse>>> getSelectedUsers(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("userIds") List<Long> userIds
    ) {
        List<UserSearchResponse> response = userSearchService.getSelectedUsers(userIds);

        return ResponseEntity.ok(
                ApiResponse.success(SELECTED_USER_SUCCESS, response)
        );
    }
}
