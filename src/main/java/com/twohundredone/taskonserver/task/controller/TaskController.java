package com.twohundredone.taskonserver.task.controller;

import com.twohundredone.taskonserver.global.dto.ApiResponse;
import com.twohundredone.taskonserver.global.enums.ResponseStatusSuccess;
import com.twohundredone.taskonserver.task.dto.TaskCreateRequest;
import com.twohundredone.taskonserver.task.dto.TaskCreateResponse;
import com.twohundredone.taskonserver.task.service.TaskService;
import com.twohundredone.taskonserver.auth.service.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
public class TaskController {

    private final TaskService taskService;

    /**
     * Task 생성
     * Parameters:
     * - @AuthenticationPrincipal: 로그인된 유저 정보
     * - @PathVariable projectId: 어떤 프로젝트에 속한 Task 인지
     * - @RequestBody TaskCreateRequest: Task 생성 정보
     */
    @PostMapping("/{projectId}/tasks")
    public ResponseEntity<ApiResponse<TaskCreateResponse>> createTask(
            @PathVariable Long projectId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody TaskCreateRequest request
    ) {

        Long userId = userDetails.getId();

        TaskCreateResponse response = taskService.createTask(projectId, userId, request);

        return ResponseEntity
                .status(ResponseStatusSuccess.TASK_CREATE_SUCCESS.getStatusCode())
                .body(ApiResponse.success(ResponseStatusSuccess.TASK_CREATE_SUCCESS, response));
    }
}
