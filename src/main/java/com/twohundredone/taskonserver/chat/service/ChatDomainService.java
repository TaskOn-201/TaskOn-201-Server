package com.twohundredone.taskonserver.chat.service;

import java.util.List;

public interface ChatDomainService {
    // Project
    void onProjectCreated(Long projectId, String projectName, Long leaderUserId);

    void onProjectMembersAdded(Long projectId, List<Long> userIds);

    void onProjectMemberRemoved(Long projectId, Long userId);

    void onProjectDeleted(Long projectId);

    // Task
    void onTaskCreated(Long taskId, String taskTitle, List<Long> participantUserIds);

    void onTaskParticipantsChanged(Long taskId, List<Long> participantUserIds);

    void onTaskDeleted(Long taskId);
}
