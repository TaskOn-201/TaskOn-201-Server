package com.twohundredone.taskonserver.task.search.dto;

public record TaskCommentCountRow(
        Long taskId,
        long commentCount
) {}
