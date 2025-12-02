package com.twohundredone.taskonserver.task.repository;

import com.twohundredone.taskonserver.task.dto.TaskUserSearchResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface TaskUserQueryRepository {
    Slice<TaskUserSearchResponse> searchProjectMembersForTask(Long projectId, String keyword, Pageable pageable);
}
