package com.twohundredone.taskonserver.task.dto;

import com.twohundredone.taskonserver.task.enums.TaskPriority;
import com.twohundredone.taskonserver.task.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

public record TaskCreateRequest(
        @NotBlank(message = "Task 제목은 필수입니다.")
        @Size(max = 255, message = "Task 제목은 최대 255자까지 가능합니다.")
        String title,
        TaskStatus status,
        TaskPriority priority,
        List<Long> participantIds,
        @NotNull(message = "시작일은 필수입니다.")
        LocalDate startDate,
        @NotNull(message = "마감일은 필수입니다.")
        LocalDate dueDate,
        @Size(max = 5000, message = "설명은 최대 5000자까지 가능합니다.")
        String description
) {}
