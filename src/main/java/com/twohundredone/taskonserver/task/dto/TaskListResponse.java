package com.twohundredone.taskonserver.task.dto;

import com.twohundredone.taskonserver.task.enums.TaskPriority;
import com.twohundredone.taskonserver.task.enums.TaskStatus;

import java.time.LocalDate;

public record TaskListResponse(
        Long taskId,
        String title,
        TaskStatus status,
        TaskPriority priority,
        LocalDate dueDate
) {
}
