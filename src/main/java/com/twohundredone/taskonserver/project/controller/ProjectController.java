package com.twohundredone.taskonserver.project.controller;

import com.twohundredone.taskonserver.global.dto.ApiResponse;
import com.twohundredone.taskonserver.global.enums.ResponseStatusSuccess;
import com.twohundredone.taskonserver.project.dto.ProjectCreateRequest;
import com.twohundredone.taskonserver.project.dto.ProjectCreateResponse;
import com.twohundredone.taskonserver.project.dto.ProjectSelectResponse;
import com.twohundredone.taskonserver.project.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @Operation(summary = "프로젝트 생성", description = "프로젝트 생성 API")
    @PostMapping
    public ResponseEntity<ApiResponse<ProjectCreateResponse>> createProject(@RequestBody ProjectCreateRequest projectCreateRequest) {
        ProjectCreateResponse response = projectService.createProject(projectCreateRequest);
        return ResponseEntity.ok(ApiResponse.success(ResponseStatusSuccess.PROJECT_CREATE, response));
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ApiResponse<ProjectSelectResponse>> selectProject(@PathVariable Long projectId){
        ProjectSelectResponse response = projectService.selectProject(projectId);
        return ResponseEntity.ok(ApiResponse.success(ResponseStatusSuccess.PROJECT_CREATE, response));
    }
}
