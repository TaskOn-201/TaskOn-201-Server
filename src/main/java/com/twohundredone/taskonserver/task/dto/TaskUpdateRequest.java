package com.twohundredone.taskonserver.task.dto;

import com.twohundredone.taskonserver.task.enums.TaskPriority;
import com.twohundredone.taskonserver.task.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public record TaskUpdateRequest(
        @NotBlank(message = "업무 제목은 비어 있을 수 없습니다.")
        String title,
        TaskStatus status,
        TaskPriority priority,
        List<Long> participantIds,
        @NotNull(message = "시작일은 필수입니다.")
        LocalDate startDate,
        @NotNull(message = "마감일은 필수입니다.")
        LocalDate dueDate,
        String description
) {
}
