package com.twohundredone.taskonserver.task.dto;

public record TaskParticipantRow(
        Long taskId,
        Long userId,
        String profileImageUrl,
        boolean assignee
) {}
