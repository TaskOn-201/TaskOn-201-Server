package com.twohundredone.taskonserver.task.repository;

import com.twohundredone.taskonserver.task.dto.TaskBoardItemDto;
import com.twohundredone.taskonserver.task.enums.TaskPriority;
import java.util.List;

public interface TaskQueryRepository {
    List<TaskBoardItemDto> findBoardItemsWithFilters(Long projectId, String title, TaskPriority priority, Long userId, boolean includeArchived);
}
