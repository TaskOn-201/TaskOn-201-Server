package com.twohundredone.taskonserver.task.search.dto;

public record TaskParticipantEnrichRow(
        Long taskId,
        boolean assignee,
        String profileImageUrl
) {}
