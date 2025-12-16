package com.twohundredone.taskonserver.chat.service;

import com.twohundredone.taskonserver.chat.dto.ChatMessageListResponse;
import com.twohundredone.taskonserver.chat.dto.ChatMessageRequest;
import com.twohundredone.taskonserver.chat.dto.ChatMessageSendResponse;
import com.twohundredone.taskonserver.chat.dto.ChatRoomListResponse;
import com.twohundredone.taskonserver.chat.entity.ChatMessage;
import com.twohundredone.taskonserver.chat.entity.ChatRoom;
import com.twohundredone.taskonserver.chat.entity.ChatUser;
import com.twohundredone.taskonserver.chat.enums.ChatType;
import com.twohundredone.taskonserver.chat.repository.ChatMessageRepository;
import com.twohundredone.taskonserver.chat.repository.ChatRoomRepository;
import com.twohundredone.taskonserver.chat.repository.ChatUserRepository;
import com.twohundredone.taskonserver.chat.util.ChatTimeFormatter;
import com.twohundredone.taskonserver.global.enums.ResponseStatusError;
import com.twohundredone.taskonserver.global.exception.CustomException;
import com.twohundredone.taskonserver.user.entity.User;
import com.twohundredone.taskonserver.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatUserRepository chatUserRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    /* =================================================
       1) 채팅방 리스트 조회 (API 스펙)
       ================================================= */
    public List<ChatRoomListResponse> getMyChatRooms(Long userId) {
        List<ChatRoom> rooms = chatRoomRepository.findMyChatRooms(userId);

        return rooms.stream()
                .map(room -> toRoomListResponse(room, userId))
                .toList();
    }

    /* =================================================
       2) 메시지 리스트 조회 (API 스펙)
       ================================================= */
    public List<ChatMessageListResponse> getMessages(Long chatRoomId, Long userId) {

        // 멤버 아니면 403 / (PROJECT/TASK는 없으면 chat_user 자동 생성)
        getOrCreateChatUser(chatRoomId, userId);

        List<ChatMessage> megs =
                chatMessageRepository.findAllByChatRoom_ChatIdOrderByCreatedAtAsc(chatRoomId);

        // sender 정보 배치 조회 (N+1 방지)
        Set<Long> senderIds = megs.stream()
                .map(ChatMessage::getSenderUserId)
                .collect(Collectors.toSet());

        Map<Long, User> userMap = userRepository.findAllById(senderIds).stream()
                .collect(Collectors.toMap(User::getUserId, u -> u));

        return megs.stream()
                .map(m -> toMessageListResponse(m, userMap))
                .toList();
    }

    /* =================================================
       3) 메시지 전송 (API 스펙)
       ================================================= */
    @Transactional
    public ChatMessageSendResponse sendMessage(Long chatRoomId, Long senderId, ChatMessageRequest request) {

        if (request == null || request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new CustomException(ResponseStatusError.CHAT_BAD_REQUEST);
        }

        // 404: 채팅방 없음
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new CustomException(ResponseStatusError.CHAT_ROOM_NOT_FOUND));

        //  멤버 아니면 403 / (PROJECT/TASK는 없으면 chat_user 자동 생성)
        getOrCreateChatUser(chatRoomId, senderId);

        ChatMessage saved = chatMessageRepository.save(
                ChatMessage.builder()
                        .chatRoom(chatRoom)
                        .senderUserId(senderId)
                        .content(request.getContent())
                        .build()
        );

        return ChatMessageSendResponse.builder()
                .messageId(saved.getChatMessageId())
                .chatRoomId(chatRoomId)
                .senderId(senderId)
                .content(saved.getContent())
                .sentTime(ChatTimeFormatter.toSentTime(saved.getCreatedAt()))
                .build();
    }

    /*
     * STOMP용 saveMessage (sendMessage 래핑)
     */
    @Transactional
    public ChatMessageSendResponse saveMessage(
            Long chatRoomId,
            Long senderId,
            ChatMessageRequest request
    ) {
        return sendMessage(chatRoomId, senderId, request);
    }

    /* =================================================
       내부 공통 로직
       ================================================= */

    /**
     * 핵심:
     * - chat_user가 있으면 그대로 OK
     * - 없으면: (1) 내가 접근 가능한 채팅방인지(findMyChatRooms로 권한 검증) 확인
     *          (2) PROJECT_GROUP/TASK_GROUP이면 chat_user를 lazy 생성
     *          (3) PERSONAL은 자동 생성 금지 → chat_user 없으면 403
     */
    @Transactional
    protected ChatUser getOrCreateChatUser(Long chatId, Long userId) {

        Optional<ChatUser> existing = chatUserRepository.findByChatIdAndUserId(chatId, userId);
        if (existing.isPresent()) return existing.get();

        ChatRoom room = chatRoomRepository.findById(chatId)
                .orElseThrow(() -> new CustomException(ResponseStatusError.CHAT_ROOM_NOT_FOUND));

        // 권한 검증: "내 채팅방 목록"에 이 방이 포함되는지로 체크
        // (PROJECT/TASK는 project_member/task_participant 조인으로 검증됨)
        // (PERSONAL은 chat_user 조인이므로 chat_user가 없으면 목록에도 안 들어옴)
        boolean allowed = chatRoomRepository.findMyChatRooms(userId).stream()
                .anyMatch(r -> Objects.equals(r.getChatId(), chatId));

        if (!allowed) {
            throw new CustomException(ResponseStatusError.CHAT_FORBIDDEN);
        }

        // PERSONAL은 자동 생성 금지 (명시적으로 personal chat 생성 API에서 만들어야 안전)
        if (room.getChatType() == ChatType.PERSONAL) {
            throw new CustomException(ResponseStatusError.CHAT_FORBIDDEN);
        }

        LocalDateTime now = LocalDateTime.now();
        return chatUserRepository.save(
                ChatUser.builder()
                        .chatId(chatId)
                        .userId(userId)
                        .lastReadAt(now)
                        .createdAt(now)
                        .updatedAt(now)
                        .build()
        );
    }

    private ChatRoomListResponse toRoomListResponse(ChatRoom room, Long meUserId) {
        Long chatId = room.getChatId();

        // participants (chat_user 기반)
        List<ChatUser> chatUsers = chatUserRepository.findAllByChatId(chatId);
        List<Long> userIds = chatUsers.stream()
                .map(ChatUser::getUserId)
                .toList();

        Map<Long, User> userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getUserId, u -> u));

        List<ChatRoomListResponse.Participant> participants = userIds.stream()
                .map(uid -> ChatRoomListResponse.Participant.builder()
                        .userId(uid)
                        .profileImageUrl(
                                Optional.ofNullable(userMap.get(uid))
                                        .map(User::getProfileImageUrl)
                                        .orElse(null)
                        )
                        .build())
                .toList();

        // lastMessage + lastMessageTime
        String lastMessage = null;
        String lastMessageTime = null;

        Optional<ChatMessage> lastOpt =
                chatMessageRepository.findTop1ByChatRoom_ChatIdOrderByCreatedAtDesc(chatId);
        if (lastOpt.isPresent()) {
            ChatMessage last = lastOpt.get();
            lastMessage = last.getContent();
            lastMessageTime = ChatTimeFormatter.toDisplayTime(last.getCreatedAt());
        }

        // unreadCount
        LocalDateTime lastReadAt = chatUserRepository.findByChatIdAndUserId(chatId, meUserId)
                .map(ChatUser::getLastReadAt)
                .orElse(LocalDateTime.MIN);

        int unreadCount =
                chatMessageRepository.countByChatRoom_ChatIdAndCreatedAtAfter(chatId, lastReadAt);

        return ChatRoomListResponse.builder()
                .chatRoomId(chatId)
                .roomName(room.getChatRoomName())
                .participants(participants)
                .lastMessage(lastMessage)
                .lastMessageTime(lastMessageTime)
                .unreadCount(unreadCount)
                .build();
    }

    private ChatMessageListResponse toMessageListResponse(
            ChatMessage m,
            Map<Long, User> userMap
    ) {
        Long senderId = m.getSenderUserId();
        User u = userMap.get(senderId);

        return ChatMessageListResponse.builder()
                .messageId(m.getChatMessageId())
                .sender(ChatMessageListResponse.Sender.builder()
                        .userId(senderId)
                        .name(u != null ? u.getName() : null)
                        .profileImageUrl(u != null ? u.getProfileImageUrl() : null)
                        .build())
                .content(m.getContent())
                .sentTime(ChatTimeFormatter.toSentTime(m.getCreatedAt()))
                .displayTime(ChatTimeFormatter.toDisplayTime(m.getCreatedAt()))
                .build();
    }

    /* =================================================
   4) PERSONAL 채팅방 생성/조회 (중복 방지)
   ================================================= */
    @Transactional
    public Long createOrGetPersonalChatRoom(Long myUserId, Long targetUserId) {

        if (myUserId == null || targetUserId == null || Objects.equals(myUserId, targetUserId)) {
            throw new CustomException(ResponseStatusError.CHAT_BAD_REQUEST);
        }

        // (선택) 상대 유저 존재 검증
        userRepository.findById(targetUserId)
                .orElseThrow(() -> new CustomException(ResponseStatusError.USER_NOT_FOUND));

        // 1) 이미 PERSONAL 채팅방이 있으면 그대로 반환
        Optional<ChatRoom> existing =
                chatRoomRepository.findPersonalChatRoom(
                        myUserId,
                        targetUserId,
                        ChatType.PERSONAL
                );

        if (existing.isPresent()) {
            return existing.get().getChatId();
        }

        //  2) 없으면 새 PERSONAL 채팅방 생성
        LocalDateTime now = LocalDateTime.now();

        ChatRoom room = chatRoomRepository.save(
                ChatRoom.builder()
                        .projectId(0L)               // ⭐ PERSONAL 더미값 (nullable=false 대응)
                        .taskId(null)
                        .chatRoomName("PERSONAL")    // FE에서 상대 이름으로 가공
                        .chatType(ChatType.PERSONAL)
                        .createdAt(now)
                        .updatedAt(now)
                        .build()
        );

        Long chatId = room.getChatId();

        // 3) chat_user 2명 생성 (명시적)
        chatUserRepository.saveAll(List.of(
                ChatUser.builder()
                        .chatId(chatId)
                        .userId(myUserId)
                        .lastReadAt(now)
                        .createdAt(now)
                        .updatedAt(now)
                        .build(),
                ChatUser.builder()
                        .chatId(chatId)
                        .userId(targetUserId)
                        .lastReadAt(now)
                        .createdAt(now)
                        .updatedAt(now)
                        .build()
        ));

        return chatId;
    }

}
