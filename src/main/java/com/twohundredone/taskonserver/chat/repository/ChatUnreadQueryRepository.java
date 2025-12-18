package com.twohundredone.taskonserver.chat.repository;

public interface ChatUnreadQueryRepository {
    int countUnreadChatsInProject(Long projectId, Long userId);
}
