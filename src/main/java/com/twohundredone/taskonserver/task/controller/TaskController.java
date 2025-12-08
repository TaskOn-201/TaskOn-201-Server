package com.twohundredone.taskonserver.task.controller;

import static com.twohundredone.taskonserver.global.enums.ResponseStatusSuccess.GET_TASK_DETAIL;
import static com.twohundredone.taskonserver.global.enums.ResponseStatusSuccess.TASK_CREATE;

import com.twohundredone.taskonserver.global.dto.ApiResponse;
import com.twohundredone.taskonserver.global.enums.ResponseStatusSuccess;
import com.twohundredone.taskonserver.task.dto.TaskCreateRequest;
import com.twohundredone.taskonserver.task.dto.TaskCreateResponse;
import com.twohundredone.taskonserver.task.dto.TaskDetailResponse;
import com.twohundredone.taskonserver.task.service.TaskService;
import com.twohundredone.taskonserver.auth.service.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects/{projectId}/tasks")
public class TaskController {

    private final TaskService taskService;

    @Operation(summary = "Task 생성", description = "새로운 Task를 생성합니다.")
    @SecurityRequirement(name = "Authorization")
    @PostMapping
    public ResponseEntity<ApiResponse<TaskCreateResponse>> createTask(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long projectId,
            @Valid @RequestBody TaskCreateRequest request
    ) {
        TaskCreateResponse response =
                taskService.createTask(userDetails.getId(), projectId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(TASK_CREATE, response));
    }

    @Operation(summary = "Task 상세 조회", description = "Task 상세 정보를 조회합니다.")
    @SecurityRequirement(name = "Authorization")
    @GetMapping("/{taskId}")
    public ResponseEntity<ApiResponse<TaskDetailResponse>> getTaskDetail(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long projectId,
            @PathVariable Long taskId
    ) {
        TaskDetailResponse response =
                taskService.getTaskDetail(userDetails.getId(), projectId, taskId);

        return ResponseEntity.ok(
                ApiResponse.success(GET_TASK_DETAIL, response)
        );
    }
}
