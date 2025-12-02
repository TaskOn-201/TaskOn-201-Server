package com.twohundredone.taskonserver.task.service;

import static com.twohundredone.taskonserver.global.enums.ResponseStatusError.PROJECT_FORBIDDEN;
import static com.twohundredone.taskonserver.global.enums.ResponseStatusError.PROJECT_NOT_FOUND;

import com.twohundredone.taskonserver.global.enums.ResponseStatusError;
import com.twohundredone.taskonserver.global.exception.CustomException;
import com.twohundredone.taskonserver.project.repository.ProjectMemberRepository;
import com.twohundredone.taskonserver.project.repository.ProjectRepository;
import com.twohundredone.taskonserver.task.dto.TaskUserSearchResponse;
import com.twohundredone.taskonserver.task.repository.TaskUserQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskUserSearchService {
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final TaskUserQueryRepository taskUserQueryRepository;

    // 사용자 검색 - 업무
    @Transactional(readOnly = true)
    public Slice<TaskUserSearchResponse> search(
            Long loginUserId,
            Long projectId,
            String keyword,
            Pageable pageable
    ) {

        projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(PROJECT_NOT_FOUND));

        // 프로젝트 멤버인지 검증
        validateProjectMember(loginUserId, projectId);

        // 검색 수행 (Slice)
        return taskUserQueryRepository.searchProjectMembersForTask(projectId, keyword, pageable);
    }


    // 로그인한 유저가 해당 프로젝트의 멤버인지 확인
    private void validateProjectMember(Long loginUserId, Long projectId) {

        boolean isMember = projectMemberRepository
                .existsByProject_ProjectIdAndUser_UserId(projectId, loginUserId);

        if (!isMember) {
            throw new CustomException(PROJECT_FORBIDDEN);
        }
    }

}
