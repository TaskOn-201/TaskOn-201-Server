package com.twohundredone.taskonserver.chat.repository;

import com.twohundredone.taskonserver.chat.dto.ChatParticipantFlatDto;
import com.twohundredone.taskonserver.chat.dto.ChatSearchResponse.ChatRoomSearchItem;
import java.util.List;

public interface ChatSearchQueryRepository {
    List<ChatRoomSearchItem> searchChatRooms(Long userId, String keyword);
    List<ChatParticipantFlatDto> findParticipantsByChatRoomIds(List<Long> chatRoomIds);
}
