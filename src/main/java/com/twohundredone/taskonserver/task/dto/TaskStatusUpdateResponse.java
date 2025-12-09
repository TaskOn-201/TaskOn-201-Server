package com.twohundredone.taskonserver.task.dto;

import com.twohundredone.taskonserver.task.enums.TaskStatus;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record TaskStatusUpdateResponse(
        Long taskId,
        Long projectId,
        TaskStatus status,
        LocalDateTime updatedAt
) {

}
