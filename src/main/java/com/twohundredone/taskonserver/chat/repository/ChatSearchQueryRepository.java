package com.twohundredone.taskonserver.chat.repository;

import com.twohundredone.taskonserver.chat.dto.ChatSearchResponse;
import com.twohundredone.taskonserver.chat.dto.ChatSearchResponse.TaskSummary;
import java.util.List;

public interface ChatSearchQueryRepository {
    List<ChatSearchResponse.UserSummary> searchUsers(
            Long userId,
            String keyword
    );

    List<TaskSummary> searchTasks(
            Long userId,
            String keyword
    );
}
