package com.twohundredone.taskonserver.task.search.event;

import lombok.Getter;

@Getter
public class TaskChangedEvent {
    private final Long taskId;
    private final ChangeType changeType;

    public enum ChangeType { CREATED, UPDATED, DELETED }

    public TaskChangedEvent(Long taskId, ChangeType changeType) {
        this.taskId = taskId;
        this.changeType = changeType;
    }
}
