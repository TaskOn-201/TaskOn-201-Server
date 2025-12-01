package com.twohundredone.taskonserver.project.controller;

import com.twohundredone.taskonserver.auth.service.CustomUserDetails;
import com.twohundredone.taskonserver.global.dto.ApiResponse;
import com.twohundredone.taskonserver.global.enums.ResponseStatusSuccess;
import com.twohundredone.taskonserver.project.dto.*;
import com.twohundredone.taskonserver.project.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @Operation(summary = "프로젝트 생성", description = "프로젝트 생성 API")
    @PostMapping
    public ResponseEntity<ApiResponse<ProjectCreateResponse>> createProject
            (@Valid @RequestBody ProjectCreateRequest projectCreateRequest, @AuthenticationPrincipal CustomUserDetails userDetails) {
        ProjectCreateResponse response = projectService.createProject(projectCreateRequest, userDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(ResponseStatusSuccess.PROJECT_CREATE, response));
    }

    @Operation(summary = "프로젝트 선택", description = "프로젝트 선택 API")
    @GetMapping("/{projectId}")
    public ResponseEntity<ApiResponse<ProjectSelectResponse>> selectProject(@PathVariable Long projectId, @AuthenticationPrincipal CustomUserDetails userDetails){
        ProjectSelectResponse response = projectService.selectProject(projectId, userDetails);
        return ResponseEntity.ok(ApiResponse.success(ResponseStatusSuccess.PROJECT_SELECT, response));
    }

    @Operation(summary = "프로젝트 목록 조회", description = "프로젝트 목록 조회 API")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProjectListResponse>>> getProjectList(@AuthenticationPrincipal CustomUserDetails userDetails){
        List<ProjectListResponse> response = projectService.getProjectList(userDetails);
        return ResponseEntity.ok(ApiResponse.success(ResponseStatusSuccess.GET_PROJECT_LIST, response));
    }

    @Operation(summary = "프로젝트 사이드바 정보", description = "프로젝트 사이드바 정보 API")
    @GetMapping("/{projectId}/sidebar")
    public ResponseEntity<ApiResponse<SidebarInfoResponse>> getSidebarInfo(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long projectId){
        SidebarInfoResponse response = projectService.getSidebarInfo(userDetails, projectId);
        return ResponseEntity.ok(ApiResponse.success(ResponseStatusSuccess.GET_SIDEBAR_INFO, response));
    }

    @Operation(summary = "프로젝트 팀원 리스트 조회", description = "프로젝트 팀원 리스트 조회 API")
    @GetMapping("/{projectId}/members")
    public ResponseEntity<ApiResponse<List<ProjectMemberListResponse>>> getProjectMemberList(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long projectId){
        List<ProjectMemberListResponse> responses = projectService.getProjectMemberList(userDetails, projectId);
        return ResponseEntity.ok(ApiResponse.success(ResponseStatusSuccess.GET_PROJECT_MEMBER, responses));
    }

    @Operation(summary = "프로젝트 설정 정보 조회", description = "프로젝트 설정 정보 조회 API")
    @GetMapping("/{projectId}/settings")
    public ResponseEntity<ApiResponse<ProjectSettingsResponseInfo>> getProjectSettingsInfo(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long projectId){
        ProjectSettingsResponseInfo response = projectService.ProjectSettingsResponseInfo(userDetails, projectId);
        return ResponseEntity.ok(ApiResponse.success(ResponseStatusSuccess.GET_PROJECT_SETTINGS, response));
    }

    @Operation(summary = "프로젝트 삭제", description = "프로젝트 삭제 API")
    @DeleteMapping("{projectId}")
    public ResponseEntity<ApiResponse<String>> deleteProject(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long projectId, @RequestBody ProjectDeleteRequest projectDeleteRequest){
        String response = projectService.deleteProject(userDetails, projectId, projectDeleteRequest);
        return ResponseEntity.ok(ApiResponse.success(ResponseStatusSuccess.DELETE_PROJECT, response));
    }
}
