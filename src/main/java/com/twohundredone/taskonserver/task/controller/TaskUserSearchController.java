package com.twohundredone.taskonserver.task.controller;

import static com.twohundredone.taskonserver.global.enums.ResponseStatusSuccess.SEARCH_USER_SUCCESS;

import com.twohundredone.taskonserver.auth.service.CustomUserDetails;
import com.twohundredone.taskonserver.common.dto.SliceResponse;
import com.twohundredone.taskonserver.global.dto.ApiResponse;
import com.twohundredone.taskonserver.global.enums.ResponseStatusSuccess;
import com.twohundredone.taskonserver.task.dto.TaskUserSearchResponse;
import com.twohundredone.taskonserver.task.service.TaskUserSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/projects/{projectId}/tasks/users")
@RequiredArgsConstructor
public class TaskUserSearchController {
    private final TaskUserSearchService taskUserSearchService;

    @Operation(summary = "사용자 검색 - 업무 배정", description = "업무 생성/수정 시 배정 가능한 프로젝트 멤버 검색 API")
    @SecurityRequirement(name = "Authorization")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<SliceResponse<TaskUserSearchResponse>>> search(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long projectId,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 20) Pageable pageable
    ) {

        Slice<TaskUserSearchResponse> response =
                taskUserSearchService.search(userDetails.getId(), projectId, keyword, pageable);

        return ResponseEntity.ok(
                ApiResponse.success(
                        SEARCH_USER_SUCCESS,
                        SliceResponse.from(response)
                )
        );
    }
}
