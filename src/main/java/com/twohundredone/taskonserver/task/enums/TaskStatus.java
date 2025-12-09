package com.twohundredone.taskonserver.task.enums;

public enum TaskStatus {
    TODO,
    IN_PROGRESS,
    COMPLETED;

    public boolean isTodo() {
        return this == TODO;
    }

    public boolean isInProgress() {
        return this == IN_PROGRESS;
    }

    public boolean isCompleted() {
        return this == COMPLETED;
    }
}
