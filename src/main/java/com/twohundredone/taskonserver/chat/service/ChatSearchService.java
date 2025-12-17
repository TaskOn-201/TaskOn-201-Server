package com.twohundredone.taskonserver.chat.service;

import com.twohundredone.taskonserver.chat.dto.ChatSearchResponse;
import com.twohundredone.taskonserver.chat.dto.ChatSearchResponse.TaskSummary;
import com.twohundredone.taskonserver.chat.repository.ChatSearchQueryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatSearchService {

    private final ChatSearchQueryRepository chatSearchQueryRepository;

    public ChatSearchResponse search(Long userId, String keyword) {

        // keyword 비어 있으면 검색 안 함
        // 추후 keyword 없을 시 최신 대화상대 10명 조회 <- 이런 조건이 생기는 것을 대비
        if (keyword == null || keyword.isBlank()) {
            return new ChatSearchResponse(
                    List.of(),
                    List.of()
            );
        }

        return new ChatSearchResponse(
                chatSearchQueryRepository.searchUsers(userId, keyword),
                chatSearchQueryRepository.searchTasks(userId, keyword)
        );
    }
}
