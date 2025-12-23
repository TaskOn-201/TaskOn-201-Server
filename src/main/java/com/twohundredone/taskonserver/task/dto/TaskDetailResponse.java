package com.twohundredone.taskonserver.task.dto;

import com.twohundredone.taskonserver.task.enums.TaskPriority;
import com.twohundredone.taskonserver.task.enums.TaskStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record TaskDetailResponse(
        Long taskId,
        Long projectId,
        String title,
        TaskStatus status,
        TaskPriority priority,
        AssigneeDto assignee,
        List<ParticipantDto> participants,
        LocalDate startDate,
        LocalDate dueDate,
        String description,

        LocalDateTime createdAt,
        LocalDateTime updatedAt
) implements TaskDetailView {
    @Builder
    public record AssigneeDto(
            Long userId,
            String name,
            String profileImageUrl
    ) {}

    @Builder
    public record ParticipantDto(
            Long userId,
            String name,
            String profileImageUrl
    ) {}
}
