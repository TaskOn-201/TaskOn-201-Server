package com.twohundredone.taskonserver.task.dto;

import com.twohundredone.taskonserver.task.enums.TaskPriority;
import com.twohundredone.taskonserver.task.enums.TaskStatus;

public record TaskBoardBaseRow(
        Long taskId,
        String title,
        TaskStatus status,
        TaskPriority priority
) {}
