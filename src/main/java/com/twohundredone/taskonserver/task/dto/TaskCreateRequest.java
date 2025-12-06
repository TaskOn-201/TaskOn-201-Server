package com.twohundredone.taskonserver.task.dto;

import com.twohundredone.taskonserver.task.enums.TaskPriority;
import com.twohundredone.taskonserver.task.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record TaskCreateRequest(
        @NotBlank String title,
        String description,
        LocalDate dueDate,
        @NotNull TaskStatus status,
        @NotNull TaskPriority priority
) {}
