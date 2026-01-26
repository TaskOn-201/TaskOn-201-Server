package com.twohundredone.taskonserver.task.service;

import com.twohundredone.taskonserver.task.enums.TaskPriority;

public class TaskCacheKeys {
    private TaskCacheKeys() {}

    public static String boardKey(Long projectId, String title, TaskPriority priority, Long userId, boolean includeArchived) {
        return "board:" + projectId
                + ":t=" + (title == null ? "" : title)
                + ":p=" + (priority == null ? "" : priority.name())
                + ":u=" + (userId == null ? "" : userId)
                + ":a=" + includeArchived;
    }
}
