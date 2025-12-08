package com.twohundredone.taskonserver.task.enums;

public enum TaskRole {
    ASSIGNEE,
    PARTICIPANT;

    public boolean isAssignee() {
        return this == ASSIGNEE;
    }

    public boolean isParticipant() {
        return this == PARTICIPANT;
    }
}
