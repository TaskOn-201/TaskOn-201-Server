package com.twohundredone.taskonserver.task.dto;

import com.twohundredone.taskonserver.task.entity.Task;
import com.twohundredone.taskonserver.task.enums.TaskPriority;
import com.twohundredone.taskonserver.task.enums.TaskStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class TaskDetailResponse {

    private Long taskId;
    private Long projectId;
    private Long writerId;
    private String taskTitle;
    private String description;
    private LocalDate startDate;
    private LocalDate dueDate;
    private TaskStatus status;
    private TaskPriority priority;

    // ⭐ Task 엔티티 -> DTO 변환용 정적 메서드
    public static TaskDetailResponse from(Task task) {
        return TaskDetailResponse.builder()
                .taskId(task.getTaskId())
                .projectId(task.getProject().getProjectId())
                .taskTitle(task.getTaskTitle())
                .description(task.getDescription())
                .startDate(task.getStartDate())
                .dueDate(task.getDueDate())
                .status(task.getStatus())
                .priority(task.getPriority())
                .build();
    }
}
