package com.twohundredone.taskonserver.project.controller;

import static com.twohundredone.taskonserver.global.enums.ResponseStatusSuccess.ADD_PROJECT_MEMBER_SUCCESS;
import static com.twohundredone.taskonserver.global.enums.ResponseStatusSuccess.DELETE_PROJECT_MEMBER_SUCCESS;

import com.twohundredone.taskonserver.auth.service.CustomUserDetails;
import com.twohundredone.taskonserver.global.dto.ApiResponse;
import com.twohundredone.taskonserver.project.dto.AddMemberRequest;
import com.twohundredone.taskonserver.project.dto.AddMemberResponse;
import com.twohundredone.taskonserver.project.service.ProjectMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects/{projectId}/members")
@RequiredArgsConstructor
public class ProjectMemberController {
    private final ProjectMemberService projectMemberService;

    @Operation(summary = "프로젝트 팀원 추가", description = "프로젝트 팀원 추가 API")
    @SecurityRequirement(name = "Authorization")
    @PostMapping
    public ResponseEntity<ApiResponse<AddMemberResponse>> addMembers(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long projectId,
            @Valid @RequestBody AddMemberRequest request
    ) {
        ApiResponse<AddMemberResponse> response =
                projectMemberService.addMembers(userDetails.getId(), projectId, request);

        return ResponseEntity
                .status(response.getStatusCode())
                .body(response);
    }

    @Operation(summary = "프로젝트 팀원 삭제", description = "프로젝트에서 특정 팀원을 삭제한다.")
    @SecurityRequirement(name = "Authorization")
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteMember(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long projectId,
            @PathVariable Long userId
    ) {
        projectMemberService.deleteMember(userDetails.getId(), projectId, userId);

        return ResponseEntity.ok(
                ApiResponse.success(DELETE_PROJECT_MEMBER_SUCCESS, null)
        );
    }
}
