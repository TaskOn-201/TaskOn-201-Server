package com.twohundredone.taskonserver.task.controller;

import static com.twohundredone.taskonserver.global.enums.ResponseStatusSuccess.GET_TASK_BOARD;
import static com.twohundredone.taskonserver.global.enums.ResponseStatusSuccess.GET_TASK_DETAIL;
import static com.twohundredone.taskonserver.global.enums.ResponseStatusSuccess.TASK_CREATE;
import static com.twohundredone.taskonserver.global.enums.ResponseStatusSuccess.TASK_DELETE;
import static com.twohundredone.taskonserver.global.enums.ResponseStatusSuccess.TASK_UPDATE;
import static com.twohundredone.taskonserver.global.enums.ResponseStatusSuccess.UPDATED_TASK_STATUS;

import com.twohundredone.taskonserver.global.dto.ApiResponse;
import com.twohundredone.taskonserver.global.enums.ResponseStatusSuccess;
import com.twohundredone.taskonserver.task.dto.TaskBoardResponse;
import com.twohundredone.taskonserver.task.dto.TaskCreateRequest;
import com.twohundredone.taskonserver.task.dto.TaskCreateResponse;
import com.twohundredone.taskonserver.task.dto.TaskDetailResponse;
import com.twohundredone.taskonserver.task.dto.TaskDetailView;
import com.twohundredone.taskonserver.task.dto.TaskStatusUpdateRequest;
import com.twohundredone.taskonserver.task.dto.TaskStatusUpdateResponse;
import com.twohundredone.taskonserver.task.dto.TaskUpdateRequest;
import com.twohundredone.taskonserver.task.enums.TaskPriority;
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
    public ResponseEntity<ApiResponse<TaskDetailView>> getTaskDetail(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long projectId,
            @PathVariable Long taskId
    ) {
        TaskDetailView response =
                taskService.getTaskDetail(userDetails.getId(), projectId, taskId);

        return ResponseEntity.ok(
                ApiResponse.success(GET_TASK_DETAIL, response)
        );
    }

    @Operation(summary = "Task 수정", description = "Task 정보를 수정합니다.")
    @SecurityRequirement(name = "Authorization")
    @PatchMapping("/{taskId}")
    public ResponseEntity<ApiResponse<TaskDetailResponse>> updateTask(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @Valid @RequestBody TaskUpdateRequest request
    ) {
        TaskDetailResponse response =
                taskService.updateTask(userDetails.getId(), projectId, taskId, request);

        return ResponseEntity.ok(
                ApiResponse.success(TASK_UPDATE, response)
        );
    }

    @Operation(summary = "Task 삭제", description = "해당 Task를 삭제합니다. (Assignee만 가능)")
    @SecurityRequirement(name = "Authorization")
    @DeleteMapping("/{taskId}")
    public ResponseEntity<ApiResponse<Void>> deleteTask(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long projectId,
            @PathVariable Long taskId
    ) {

        taskService.deleteTask(userDetails.getId(), projectId, taskId);

        return ResponseEntity.ok(
                ApiResponse.success(TASK_DELETE, null)
        );
    }

    @Operation(summary = "Task 보드 조회", description = "상태별 Task 목록을 반환합니다.")
    @SecurityRequirement(name = "Authorization")
    @GetMapping("/board")
    public ResponseEntity<ApiResponse<TaskBoardResponse>> getTaskBoard(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long projectId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "false") boolean includeArchived
    ) {

        TaskBoardResponse response = taskService.getTaskBoard(
                userDetails.getId(), projectId, title, priority, userId, includeArchived
        );

        return ResponseEntity.ok(
                ApiResponse.success(GET_TASK_BOARD, response)
        );
    }

    @Operation(summary = "Task 상태 변경", description = "드래그 앤 드롭으로 Task 상태를 변경합니다.")
    @SecurityRequirement(name = "Authorization")
    @PatchMapping("/{taskId}/status")
    public ResponseEntity<ApiResponse<TaskStatusUpdateResponse>> updateTaskStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @Valid @RequestBody TaskStatusUpdateRequest request
    ) {
        TaskStatusUpdateResponse response =
                taskService.updateTaskStatus(userDetails.getId(), projectId, taskId, request);

        return ResponseEntity.ok(
                ApiResponse.success(UPDATED_TASK_STATUS, response)
        );
    }


}
