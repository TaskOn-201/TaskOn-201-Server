package com.twohundredone.taskonserver.project.controller;

import com.twohundredone.taskonserver.auth.service.CustomUserDetails;
import com.twohundredone.taskonserver.global.dto.ApiResponse;
import com.twohundredone.taskonserver.global.enums.ResponseStatusSuccess;
import com.twohundredone.taskonserver.project.dto.*;
import com.twohundredone.taskonserver.project.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @Operation(summary = "프로젝트 생성", description = "프로젝트 생성 API")
    @PostMapping
    public ResponseEntity<ApiResponse<ProjectCreateResponse>> createProject(@RequestBody ProjectCreateRequest projectCreateRequest, @AuthenticationPrincipal CustomUserDetails userDetails) {
        ProjectCreateResponse response = projectService.createProject(projectCreateRequest, userDetails);
        return ResponseEntity.ok(ApiResponse.success(ResponseStatusSuccess.PROJECT_CREATE, response));
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ApiResponse<ProjectSelectResponse>> selectProject(@PathVariable Long projectId, @AuthenticationPrincipal CustomUserDetails userDetails){
        ProjectSelectResponse response = projectService.selectProject(projectId, userDetails);
        return ResponseEntity.ok(ApiResponse.success(ResponseStatusSuccess.PROJECT_SELECT, response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TaskListResponse>>> getProjectList(@AuthenticationPrincipal CustomUserDetails userDetails){
        List<TaskListResponse> response = projectService.getProject(userDetails);
        return ResponseEntity.ok(ApiResponse.success(ResponseStatusSuccess.GET_PROJECT_LIST, response));
    }

    @GetMapping("/{projectId}/sidebar")
    public ResponseEntity<ApiResponse<SidebarInfoResponse>> getSidebarInfo(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long projectId){
        SidebarInfoResponse response = projectService.getSidebarInfo(userDetails, projectId);
        return ResponseEntity.ok(ApiResponse.success(ResponseStatusSuccess.GET_SIDEBAR_INFO, response));
    }
    
    @GetMapping("/{projectId}/members")
    public ResponseEntity<ApiResponse<List<ProjectMemberListResponse>>> getProjectMemberList(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long projectId){
        List<ProjectMemberListResponse> responses = projectService.getProjectMemberList(userDetails, projectId);
        return ResponseEntity.ok(ApiResponse.success(ResponseStatusSuccess.GET_PROJECT_MEMBER, responses));
    }

    @GetMapping("/{projectId}/settings")
    public ResponseEntity<ApiResponse<ProjectSettingsResponseInfo>> getProjectSettingsInfo(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long projectId){
        ProjectSettingsResponseInfo response = projectService.ProjectSettingsResponseInfo(userDetails, projectId);
        return ResponseEntity.ok(ApiResponse.success(ResponseStatusSuccess.GET_PROJECT_SETTINGS, response));
    }
}
