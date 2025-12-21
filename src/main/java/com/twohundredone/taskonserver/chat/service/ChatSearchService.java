package com.twohundredone.taskonserver.chat.service;

import static com.twohundredone.taskonserver.chat.enums.ChatType.PERSONAL;

import com.twohundredone.taskonserver.chat.dto.ChatParticipantFlatDto;
import com.twohundredone.taskonserver.chat.dto.ChatSearchResponse;
import com.twohundredone.taskonserver.chat.dto.ChatSearchResponse.ChatParticipantDto;
import com.twohundredone.taskonserver.chat.dto.ChatSearchResponse.ChatRoomSearchItem;
import com.twohundredone.taskonserver.chat.repository.ChatSearchQueryRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatSearchService {

    private final ChatSearchQueryRepository chatSearchQueryRepository;

    public ChatSearchResponse search(Long userId, String keyword) {

        List<ChatRoomSearchItem> rooms =
                chatSearchQueryRepository.searchChatRooms(userId, keyword);

        if (rooms.isEmpty()) {
            return new ChatSearchResponse(List.of());
        }

        List<Long> roomIds = rooms.stream()
                .map(ChatRoomSearchItem::chatRoomId)
                .toList();

        List<ChatParticipantFlatDto> flats =
                chatSearchQueryRepository.findParticipantsByChatRoomIds(roomIds);

        // chatRoomId -> participants
        Map<Long, List<ChatParticipantDto>> participantMap =
                flats.stream()
                        .collect(Collectors.groupingBy(
                                ChatParticipantFlatDto::chatRoomId,
                                Collectors.mapping(
                                        f -> new ChatParticipantDto(
                                                f.userId(),
                                                f.name(),
                                                f.profileImageUrl()
                                        ),
                                        Collectors.toList()
                                )
                        ));

        // 채팅방 DTO에 주입
        List<ChatRoomSearchItem> result =
                rooms.stream()
                        .map(room -> {

                            List<ChatParticipantDto> participants =
                                    participantMap.getOrDefault(
                                            room.chatRoomId(),
                                            List.of()
                                    );

                            String roomName = room.roomName();

                            if (room.chatType() == PERSONAL) {
                                roomName = participants.stream()
                                        .filter(p -> !p.userId().equals(userId))
                                        .findFirst()
                                        .map(ChatParticipantDto::name)
                                        .orElse("알 수 없는 사용자");
                            }

                            return ChatRoomSearchItem.builder()
                                    .chatRoomId(room.chatRoomId())
                                    .roomName(roomName)
                                    .chatType(room.chatType())
                                    .relatedTaskId(room.relatedTaskId())
                                    .lastMessage(room.lastMessage())
                                    .lastMessageAt(room.lastMessageAt())
                                    .unreadCount(room.unreadCount())
                                    .participants(participants)
                                    .build();
                        })
                        .toList();

        return new ChatSearchResponse(result);
    }

}
