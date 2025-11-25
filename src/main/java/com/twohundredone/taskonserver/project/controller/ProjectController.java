package com.twohundredone.taskonserver.project.controller;

import com.twohundredone.taskonserver.auth.service.CustomUserDetails;
import com.twohundredone.taskonserver.global.dto.ApiResponse;
import com.twohundredone.taskonserver.global.enums.ResponseStatusSuccess;
import com.twohundredone.taskonserver.project.dto.ProjectCreateRequest;
import com.twohundredone.taskonserver.project.dto.ProjectCreateResponse;
import com.twohundredone.taskonserver.project.dto.ProjectSelectResponse;
import com.twohundredone.taskonserver.project.dto.TaskListResponse;
import com.twohundredone.taskonserver.project.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
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
}
