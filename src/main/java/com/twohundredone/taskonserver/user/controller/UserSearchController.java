package com.twohundredone.taskonserver.user.controller;

import static com.twohundredone.taskonserver.global.enums.ResponseStatusSuccess.SEARCH_USER_SUCCESS;

import com.twohundredone.taskonserver.auth.service.CustomUserDetails;
import com.twohundredone.taskonserver.common.dto.SliceResponse;
import com.twohundredone.taskonserver.global.dto.ApiResponse;
import com.twohundredone.taskonserver.project.service.ProjectUserSearchService;
import com.twohundredone.taskonserver.user.dto.UserSearchResponse;
import com.twohundredone.taskonserver.user.service.UserSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects/{projectId}/members")
@RequiredArgsConstructor
public class UserSearchController {

    private final ProjectUserSearchService projectUserSearchService;

    @Operation(summary = "사용자 검색 - 프로젝트", description = "프로젝트 사용자 검색 API")
    @SecurityRequirement(name = "Authorization")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<SliceResponse<UserSearchResponse>>> search(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long projectId,
            @RequestParam String keyword,
            @PageableDefault(size = 20) Pageable pageable
    )  {
        Slice<UserSearchResponse> response =
                projectUserSearchService.search(userDetails.getId(), projectId, keyword, pageable);

        return ResponseEntity.ok(
                ApiResponse.success(
                        SEARCH_USER_SUCCESS,
                        SliceResponse.from(response)
                )
        );
    }
}
