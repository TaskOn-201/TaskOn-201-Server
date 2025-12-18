package com.twohundredone.taskonserver.user.service;

import static com.twohundredone.taskonserver.global.enums.ResponseStatusError.INVALID_KEYWORD;

import com.twohundredone.taskonserver.global.exception.CustomException;
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

    private final UserQueryRepository userQueryRepository;


    // 채팅용 (전체 사용자 검색)
    public Slice<UserSearchResponse> searchForChat(
            Long loginUserId,
            String keyword,
            Pageable pageable
    ) {
        if (keyword == null || keyword.trim().length() < 1) {
            throw new CustomException(INVALID_KEYWORD);
        }
        return userQueryRepository.searchUsers(
                loginUserId,
                null,   // projectId 없음
                keyword,
                pageable
        );
    }

    // 선택된 사용자 조회
    public List<UserSearchResponse> getSelectedUsers(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return List.of();
        }

        return userQueryRepository.findUsersByIds(userIds);
    }
}
