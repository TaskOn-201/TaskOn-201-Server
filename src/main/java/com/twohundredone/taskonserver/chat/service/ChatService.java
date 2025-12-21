package com.twohundredone.taskonserver.chat.service;

import static com.twohundredone.taskonserver.chat.enums.ChatType.PERSONAL;
import static com.twohundredone.taskonserver.global.enums.ResponseStatusError.CHAT_BAD_REQUEST;
import static com.twohundredone.taskonserver.global.enums.ResponseStatusError.CHAT_FORBIDDEN;
import static com.twohundredone.taskonserver.global.enums.ResponseStatusError.CHAT_ROOM_NOT_FOUND;
import static com.twohundredone.taskonserver.global.enums.ResponseStatusError.USER_NOT_FOUND;

import com.twohundredone.taskonserver.chat.dto.ChatMessageListResponse;
import com.twohundredone.taskonserver.chat.dto.ChatMessageRequest;
import com.twohundredone.taskonserver.chat.dto.ChatMessageSendResponse;
import com.twohundredone.taskonserver.chat.dto.ChatRoomListResponse;
import com.twohundredone.taskonserver.chat.dto.ChatRoomSummaryDto;
import com.twohundredone.taskonserver.chat.entity.ChatMessage;
import com.twohundredone.taskonserver.chat.entity.ChatRoom;
import com.twohundredone.taskonserver.chat.entity.ChatUser;
import com.twohundredone.taskonserver.chat.repository.ChatMessageRepository;
import com.twohundredone.taskonserver.chat.repository.ChatRoomQueryRepository;
import com.twohundredone.taskonserver.chat.repository.ChatRoomRepository;
import com.twohundredone.taskonserver.chat.repository.ChatUserRepository;
import com.twohundredone.taskonserver.chat.util.ChatTimeFormatter;
import com.twohundredone.taskonserver.global.exception.CustomException;
import com.twohundredone.taskonserver.user.entity.User;
import com.twohundredone.taskonserver.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatUserRepository chatUserRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final ChatRoomQueryRepository chatRoomQueryRepository;

    // 채팅방 리스트
    public List<ChatRoomListResponse> getMyChatRooms(Long userId) {
        List<ChatRoomSummaryDto> summaries =
                chatRoomQueryRepository.findMyChatRoomSummaries(userId);

        // 1) roomIds 수집
        List<Long> roomIds = summaries.stream()
                .map(ChatRoomSummaryDto::chatRoomId)
                .toList();

        // 방이 없으면 바로 반환
        if (roomIds.isEmpty()) return List.of();

        // chat_user를 roomIds로 한번에 조회
        List<ChatUser> chatUsers = chatUserRepository.findAllByChatRoom_ChatIdIn(roomIds);

        // userIds 수집 후 user 테이블 한번에 조회
        Set<Long> participantUserIds = chatUsers.stream()
                .map(ChatUser::getUserId)
                .collect(Collectors.toSet());

        Map<Long, User> userMap = userRepository.findAllById(participantUserIds).stream()
                .collect(Collectors.toMap(User::getUserId, u -> u));

        // roomId -> participants 리스트로 그룹핑
        Map<Long, List<ChatRoomListResponse.Participant>> participantsByRoomId =
                chatUsers.stream()
                        .filter(cu -> !cu.getUserId().equals(userId))
                        .collect(Collectors.groupingBy(
                                cu -> cu.getChatRoom().getChatId(),
                                Collectors.mapping(cu -> {
                                    User u = userMap.get(cu.getUserId());
                                    return new ChatRoomListResponse.Participant(
                                            cu.getUserId(),
                                            u != null ? u.getName() : null,
                                            u != null ? u.getProfileImageUrl() : null
                                    );
                                }, Collectors.toList())
                        ));

        // DTO 조립 (participants null 방지: emptyList)
        return summaries.stream()
                .map(dto -> {

                    List<ChatRoomListResponse.Participant> participants =
                            participantsByRoomId.getOrDefault(dto.chatRoomId(), List.of());

                    String roomName;

                    if (dto.chatType() == PERSONAL) {
                        // 1:1 채팅 → 상대방 이름
                        roomName = participants.isEmpty()
                                ? "알 수 없는 사용자"
                                : participants.get(0).name();
                    } else {
                        // PROJECT / TASK → 기존 roomName 사용
                        roomName = dto.roomName();
                    }

                    return ChatRoomListResponse.builder()
                            .chatRoomId(dto.chatRoomId())
                            .roomName(roomName)
                            .chatType(dto.chatType())
                            .participants(participants)
                            .lastMessage(dto.lastMessage())
                            .lastMessageTime(
                                    dto.lastMessageAt() != null
                                            ? ChatTimeFormatter.toDisplayTime(dto.lastMessageAt())
                                            : null
                            )
                            .lastMessageAt(dto.lastMessageAt())
                            .unreadCount(dto.unreadCount())
                            .build();
                })
                .toList();
    }

    // 메시지 리스트
    @Transactional
    public List<ChatMessageListResponse> getMessages(Long chatRoomId, Long userId) {

        ChatUser chatUser = getOrCreateChatUser(chatRoomId, userId);

        List<ChatMessage> messages =
                chatMessageRepository.findAllByChatRoom_ChatIdOrderByCreatedAtAsc(chatRoomId);

        Set<Long> senderIds = messages.stream()
                .map(ChatMessage::getSenderUserId)
                .collect(Collectors.toSet());

        Map<Long, User> userMap = userRepository.findAllById(senderIds).stream()
                .collect(Collectors.toMap(User::getUserId, u -> u));

        // 읽음 처리 갱신
        chatUser.updateLastReadAt(LocalDateTime.now());

        return messages.stream()
                .map(m -> toMessageListResponse(m, userMap))
                .toList();
    }

    // 메시지 전송
    @Transactional
    public ChatMessageSendResponse sendMessage(
            Long chatRoomId,
            Long senderId,
            ChatMessageRequest request
    ) {
        if (request == null || request.content().isBlank()) {
            throw new CustomException(CHAT_BAD_REQUEST);
        }

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new CustomException(CHAT_ROOM_NOT_FOUND));

        ChatUser chatUser = getOrCreateChatUser(chatRoomId, senderId);

        ChatMessage saved = chatMessageRepository.save(
                ChatMessage.builder()
                        .chatRoom(chatRoom)
                        .senderUserId(senderId)
                        .content(request.content())
                        .build()
        );

        // 내가 보낸 메시지는 즉시 읽음 처리
        chatUser.updateLastReadAt(saved.getCreatedAt());

        return ChatMessageSendResponse.builder()
                .messageId(saved.getChatMessageId())
                .chatRoomId(chatRoomId)
                .senderId(senderId)
                .content(saved.getContent())
                .sentTime(ChatTimeFormatter.toSentTime(saved.getCreatedAt()))
                .createdAt(saved.getCreatedAt())
                .build();
    }

    @Transactional
    public Long createOrGetPersonalChatRoom(Long myUserId, Long targetUserId) {

        if (myUserId == null || targetUserId == null || myUserId.equals(targetUserId)) {
            throw new CustomException(CHAT_BAD_REQUEST);
        }

        userRepository.findById(targetUserId)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        Optional<ChatRoom> existing =
                chatRoomRepository.findPersonalChatRoom(myUserId, targetUserId, PERSONAL);

        if (existing.isPresent()) {
            return existing.get().getChatId();
        }

        ChatRoom room = chatRoomRepository.save(
                ChatRoom.builder()
                        .chatRoomName("PERSONAL")
                        .chatType(PERSONAL)
                        .build()
        );

        chatUserRepository.saveAll(List.of(
                ChatUser.builder()
                        .chatRoom(room)
                        .userId(myUserId)
                        .lastReadAt(LocalDateTime.now())
                        .build(),
                ChatUser.builder()
                        .chatRoom(room)
                        .userId(targetUserId)
                        .lastReadAt(LocalDateTime.now())
                        .build()
        ));

        return room.getChatId();
    }


    // 공통 로직
    @Transactional
    protected ChatUser getOrCreateChatUser(Long chatId, Long userId) {

        Optional<ChatUser> existing =
                chatUserRepository.findByChatRoom_ChatIdAndUserId(chatId, userId);

        if (existing.isPresent()) return existing.get();

        ChatRoom room = chatRoomRepository.findById(chatId)
                .orElseThrow(() -> new CustomException(CHAT_ROOM_NOT_FOUND));

        // PERSONAL은 자동 생성 금지
        if (room.getChatType() == PERSONAL) {
            throw new CustomException(CHAT_FORBIDDEN);
        }

        // PROJECT / TASK 접근 권한 검증
        boolean allowed =
                chatRoomRepository.findMyChatRooms(userId).stream()
                        .anyMatch(r -> r.getChatId().equals(chatId));

        if (!allowed) {
            throw new CustomException(CHAT_FORBIDDEN);
        }

        ChatUser chatUser = ChatUser.builder()
                .chatRoom(room)
                .userId(userId)
                .lastReadAt(LocalDateTime.now())
                .build();

        return chatUserRepository.save(chatUser);
    }

    private ChatMessageListResponse toMessageListResponse(
            ChatMessage m,
            Map<Long, User> userMap
    ) {
        User u = userMap.get(m.getSenderUserId());

        return ChatMessageListResponse.builder()
                .messageId(m.getChatMessageId())
                .sender(ChatMessageListResponse.Sender.builder()
                        .userId(m.getSenderUserId())
                        .name(u != null ? u.getName() : null)
                        .profileImageUrl(u != null ? u.getProfileImageUrl() : null)
                        .build())
                .content(m.getContent())
                .sentTime(ChatTimeFormatter.toSentTime(m.getCreatedAt()))
                .displayTime(ChatTimeFormatter.toDisplayTime(m.getCreatedAt()))
                .build();
    }

    @Transactional(readOnly = true)
    public void validateChatRoomAccess(Long chatRoomId, Long userId) {

        ChatRoom room = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() ->
                        new CustomException(CHAT_ROOM_NOT_FOUND)
                );

        // PERSONAL: chat_user 필수
        if (room.getChatType() == PERSONAL) {
            boolean isMember =
                    chatUserRepository.existsByChatRoom_ChatIdAndUserId(chatRoomId, userId);

            if (!isMember) {
                throw new CustomException(CHAT_FORBIDDEN);
            }
            return;
        }

        // PROJECT / TASK: "내 채팅방 목록" 기준으로 검증
        boolean allowed =
                chatRoomRepository.findMyChatRooms(userId).stream()
                        .anyMatch(r -> r.getChatId().equals(chatRoomId));

        if (!allowed) {
            throw new CustomException(CHAT_FORBIDDEN);
        }
    }

    @Transactional(readOnly = true)
    public List<Long> getParticipantUserIds(Long chatRoomId) {
//        return chatUserRepository.findAllByChatRoom_ChatId(chatRoomId)
//                .stream()
//                .map(ChatUser::getUserId)
//                .toList();

        List<Long> ids = chatUserRepository.findAllByChatRoom_ChatId(chatRoomId)
                .stream()
                .map(ChatUser::getUserId)
                .toList();

        log.info("🟡 [ROOM-LIST] DB participants for chatRoomId={} -> {}",
                chatRoomId, ids);

        return ids;
    }

}
