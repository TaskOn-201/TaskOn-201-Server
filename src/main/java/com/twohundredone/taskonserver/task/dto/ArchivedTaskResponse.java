package com.twohundredone.taskonserver.task.dto;

import com.twohundredone.taskonserver.task.entity.Task;
import com.twohundredone.taskonserver.task.enums.TaskPriority;
import com.twohundredone.taskonserver.task.enums.TaskStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ArchivedTaskResponse(
        Long taskId,
        Long projectId,
        String title,
        TaskStatus status,
        TaskPriority priority,
        String description,
        LocalDate startDate,
        LocalDate dueDate,
        LocalDateTime archivedAt,
        String archivedReason
) implements TaskDetailView {

    public static ArchivedTaskResponse from(Task task, String archivedReason) {
        return ArchivedTaskResponse.builder()
                .taskId(task.getTaskId())
                .projectId(task.getProject().getProjectId())
                .title(task.getTaskTitle())
                .status(task.getStatus())
                .priority(task.getPriority())
                .description(task.getDescription())
                .startDate(task.getStartDate())
                .dueDate(task.getDueDate())
                .archivedAt(task.getModifiedAt()) // archive 시점
                .archivedReason(archivedReason)
                .build();
    }
}
