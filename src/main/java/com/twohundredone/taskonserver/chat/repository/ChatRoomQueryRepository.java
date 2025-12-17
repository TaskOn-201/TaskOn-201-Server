package com.twohundredone.taskonserver.chat.repository;

import com.twohundredone.taskonserver.chat.dto.ChatRoomSummaryDto;
import java.util.List;

public interface ChatRoomQueryRepository {
    List<ChatRoomSummaryDto> findMyChatRoomSummaries(Long userId);
}
