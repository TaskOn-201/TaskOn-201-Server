package com.twohundredone.taskonserver.task.controller;

import static com.twohundredone.taskonserver.global.enums.ResponseStatusSuccess.TASK_CREATE;

import com.twohundredone.taskonserver.global.dto.ApiResponse;
import com.twohundredone.taskonserver.global.enums.ResponseStatusSuccess;
import com.twohundredone.taskonserver.task.dto.TaskCreateRequest;
import com.twohundredone.taskonserver.task.dto.TaskCreateResponse;
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
}
