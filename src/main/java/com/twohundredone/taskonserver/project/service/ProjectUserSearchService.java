package com.twohundredone.taskonserver.project.service;

import static com.twohundredone.taskonserver.global.enums.ResponseStatusError.FORBIDDEN;
import static com.twohundredone.taskonserver.global.enums.ResponseStatusError.INVALID_KEYWORD;
import static com.twohundredone.taskonserver.global.enums.ResponseStatusError.PROJECT_NOT_FOUND;

import com.twohundredone.taskonserver.global.exception.CustomException;
import com.twohundredone.taskonserver.project.enums.Role;
import com.twohundredone.taskonserver.project.repository.ProjectMemberRepository;
import com.twohundredone.taskonserver.project.repository.ProjectRepository;
import com.twohundredone.taskonserver.user.dto.UserSearchResponse;
import com.twohundredone.taskonserver.user.repository.UserQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectUserSearchService {
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserQueryRepository userQueryRepository;

    public Slice<UserSearchResponse> search(
            Long loginUserId,
            Long projectId,
            String keyword,
            Pageable pageable
    ) {
        if (keyword == null || keyword.trim().isBlank()) {
            throw new CustomException(INVALID_KEYWORD);
        }

        projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(PROJECT_NOT_FOUND));

        validateProjectAccess(loginUserId, projectId);

        return userQueryRepository.searchUsers(
                loginUserId,
                projectId,
                keyword,
                pageable
        );
    }

    private void validateProjectAccess(Long loginUserId, Long projectId) {
        boolean isLeader =
                projectMemberRepository.existsByProject_ProjectIdAndUser_UserIdAndRole(
                        projectId, loginUserId, Role.LEADER
                );

        if (!isLeader) {
            throw new CustomException(FORBIDDEN);
        }
    }
}
