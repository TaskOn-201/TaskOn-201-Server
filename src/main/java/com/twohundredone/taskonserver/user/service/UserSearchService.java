package com.twohundredone.taskonserver.user.service;

import static com.twohundredone.taskonserver.global.enums.ResponseStatusError.FORBIDDEN;

import com.twohundredone.taskonserver.global.enums.ResponseStatusError;
import com.twohundredone.taskonserver.global.exception.CustomException;
import com.twohundredone.taskonserver.project.repository.ProjectMemberRepository;
import com.twohundredone.taskonserver.user.dto.UserSearchResponse;
import com.twohundredone.taskonserver.user.repository.UserQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserSearchService {

    private final UserQueryRepository userQueryRepository;
    private final ProjectMemberRepository projectMemberRepository;

    public Slice<UserSearchResponse> search(Long loginUserId, Long projectId, String keyword, Pageable pageable) {

        validateProjectAccess(loginUserId, projectId);

        return userQueryRepository.searchUsers(loginUserId, projectId, keyword, pageable);
    }

    // 접근 권한 검증
    private void validateProjectAccess(Long loginUserId, Long projectId) {
        if (!projectMemberRepository.existsByProject_ProjectIdAndUser_UserId(projectId, loginUserId)) {
            throw new CustomException(FORBIDDEN);
        }
    }

}
