package com.twohundredone.taskonserver.task.dto;

import com.twohundredone.taskonserver.task.entity.Task;
import com.twohundredone.taskonserver.task.enums.TaskPriority;
import com.twohundredone.taskonserver.task.enums.TaskStatus;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
public record TaskCreateResponse(
        Long taskId,
        Long projectId,
        String title,
        TaskStatus status,
        TaskPriority priority,
        Long assigneeId,
        List<Long> participantIds,
        LocalDate startDate,
        LocalDate dueDate,
        String description
) { }

