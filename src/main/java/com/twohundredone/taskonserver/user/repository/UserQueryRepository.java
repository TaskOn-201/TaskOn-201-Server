package com.twohundredone.taskonserver.user.repository;

import com.twohundredone.taskonserver.user.dto.UserSearchResponse;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface UserQueryRepository {

    Slice<UserSearchResponse> searchUsers(Long loginUserId, Long projectId, String keyword, Pageable pageable);

    // 선택된 사용자 리스트 조회
    List<UserSearchResponse> findUsersByIds(List<Long> userIds);
}
