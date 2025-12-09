package com.twohundredone.taskonserver.task.dto;

import com.twohundredone.taskonserver.task.enums.TaskPriority;
import com.twohundredone.taskonserver.task.enums.TaskStatus;
import java.util.List;
import lombok.Builder;

@Builder
public record TaskBoardItemDto(
        Long taskId,
        String title,
        TaskStatus status,
        TaskPriority priority,
        String assigneeProfileImageUrl,
        List<String> participantProfileImageUrls,
        int commentCount
) {
}
