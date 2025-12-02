package com.twohundredone.taskonserver.user.service;

import static com.twohundredone.taskonserver.global.enums.ResponseStatusError.FORBIDDEN;
import static com.twohundredone.taskonserver.global.enums.ResponseStatusError.PROJECT_NOT_FOUND;

import com.twohundredone.taskonserver.global.enums.ResponseStatusError;
import com.twohundredone.taskonserver.global.exception.CustomException;
import com.twohundredone.taskonserver.project.enums.Role;
import com.twohundredone.taskonserver.project.repository.ProjectMemberRepository;
import com.twohundredone.taskonserver.project.repository.ProjectRepository;
import com.twohundredone.taskonserver.user.dto.UserSearchResponse;
import com.twohundredone.taskonserver.user.repository.UserQueryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserSearchService {

    private final ProjectRepository projectRepository;
    private final UserQueryRepository userQueryRepository;
    private final ProjectMemberRepository projectMemberRepository;

    // 사용자 검색 - 프로젝트
    public Slice<UserSearchResponse> search(Long loginUserId, Long projectId, String keyword, Pageable pageable) {
        projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(PROJECT_NOT_FOUND));

        validateProjectAccess(loginUserId, projectId);

        return userQueryRepository.searchUsers(loginUserId, projectId, keyword, pageable);
    }

    // 선택된 사용자 조회
    public List<UserSearchResponse> getSelectedUsers(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return List.of();
        }

        return userQueryRepository.findUsersByIds(userIds);
    }

    // 프로젝트 접근 권한 검증
    private void validateProjectAccess(Long loginUserId, Long projectId) {
        boolean isLeader = projectMemberRepository.existsByProject_ProjectIdAndUser_UserIdAndRole(
                projectId, loginUserId, Role.LEADER
        );

        if (!isLeader) {
            throw new CustomException(FORBIDDEN);  // 403
        }
    }

}
