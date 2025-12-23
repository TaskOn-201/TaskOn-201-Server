package com.twohundredone.taskonserver.task.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record TaskBoardResponse(
        List<TaskBoardItemDto> todo,
        List<TaskBoardItemDto> inProgress,
        List<TaskBoardItemDto> completed,
        List<TaskBoardItemDto> archived
) {

}
