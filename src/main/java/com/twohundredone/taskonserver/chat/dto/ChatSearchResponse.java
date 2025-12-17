package com.twohundredone.taskonserver.chat.dto;

import java.util.List;

public record ChatSearchResponse(
        List<UserSummary> users,
        List<TaskSummary> tasks
) {

    public record UserSummary(
            Long userId,
            String name,
            String profileImageUrl
    ) {}

    public record TaskSummary(
            Long taskId,
            Long projectId,
            String taskTitle,
            String priority
    ) {}
}
