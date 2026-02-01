package com.twohundredone.taskonserver.task.service;

import static com.twohundredone.taskonserver.global.enums.ResponseStatusError.PROJECT_FORBIDDEN;

import com.twohundredone.taskonserver.global.exception.CustomException;
import com.twohundredone.taskonserver.project.repository.ProjectMemberRepository;
import com.twohundredone.taskonserver.task.dto.TaskBoardItemDto;
import com.twohundredone.taskonserver.task.enums.TaskPriority;
import com.twohundredone.taskonserver.task.enums.TaskStatus;
import com.twohundredone.taskonserver.task.search.TaskSearchQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskSearchService {
    private final ProjectMemberRepository projectMemberRepository;
    private final TaskSearchQueryRepository taskSearchQueryRepository;

    public Page<TaskBoardItemDto> search(
            Long loginUserId,
            Long projectId,
            String keyword,
            TaskPriority priority,
            TaskStatus status,
            Long assigneeId,
            int page,
            int size
    ) {
        // 권한 체크
        projectMemberRepository.findByProject_ProjectIdAndUser_UserId(projectId, loginUserId)
                .orElseThrow(() -> new CustomException(PROJECT_FORBIDDEN));

        return taskSearchQueryRepository.search(
                projectId,
                keyword,
                priority,
                status,
                assigneeId,
                page,
                size
        );
    }
}
