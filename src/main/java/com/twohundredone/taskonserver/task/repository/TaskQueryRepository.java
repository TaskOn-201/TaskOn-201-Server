package com.twohundredone.taskonserver.task.repository;

import com.twohundredone.taskonserver.task.entity.Task;
import com.twohundredone.taskonserver.task.enums.TaskPriority;
import java.util.List;

public interface TaskQueryRepository {
    List<Task> findTasksWithFilters(Long projectId, String title, TaskPriority priority, Long userId);
}
