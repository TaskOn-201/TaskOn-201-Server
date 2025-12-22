package com.twohundredone.taskonserver.chat.controller;

import static com.twohundredone.taskonserver.global.enums.ResponseStatusError.UNAUTHORIZED;

import com.twohundredone.taskonserver.chat.dto.ChatMessageRequest;
import com.twohundredone.taskonserver.chat.dto.ChatMessageSendResponse;
import com.twohundredone.taskonserver.chat.dto.ChatRoomUpdateEvent;
import com.twohundredone.taskonserver.chat.dto.StompErrorResponse;
import com.twohundredone.taskonserver.chat.service.ChatService;
import com.twohundredone.taskonserver.chat.util.ChatTimeFormatter;
import com.twohundredone.taskonserver.global.exception.CustomException;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Slf4j
@Controller
@RequiredArgsConstructor
@SecurityRequirement(name = "Authorization")
public class ChatStompController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    // SEND: /app/chat/rooms/{chatId}
    // SUB:  /topic/chat/rooms/{chatId}

    @MessageMapping("/chat/rooms/{chatId}")
    public void sendMessage(
            @DestinationVariable Long chatId,
            @Payload ChatMessageRequest request,
            Principal principal
    ) {

        log.info("🔥 STOMP SEND arrived chatId={}, principal={}", chatId, principal);
        // STOMP 레벨 인증 가드
        if (principal == null || principal.getName() == null) {
            messagingTemplate.convertAndSend(
                    "/queue/errors",
                    new StompErrorResponse(
                            UNAUTHORIZED.name(),
                            UNAUTHORIZED.getMessage()
                    )
            );
            return;
        }

        Long senderUserId = Long.parseLong(principal.getName());

        try {
            // 도메인 로직
            ChatMessageSendResponse saved =
                    chatService.sendMessage(chatId, senderUserId, request);

            // 정상 브로드캐스트
            messagingTemplate.convertAndSend(
                    "/topic/chat/rooms/" + chatId,
                    saved
            );

            // 채팅방 리스트 실시간 갱신 이벤트
            List<Long> participantIds =
                    chatService.getParticipantUserIds(chatId);

            for (Long userId : participantIds) {
                log.info(
                        "🟡 [ROOM-LIST] try send to userId={} (sender={})",
                        userId,
                        senderUserId
                );
                // 보낸 사람 본인은 제외
                if (!userId.equals(senderUserId)) {
                    log.info(
                            "🟥 [ROOM-LIST SEND] toUser={}, chatRoomId={}, lastMessage={}",
                            userId,
                            chatId,
                            saved.content()
                    );
                    messagingTemplate.convertAndSendToUser(
                            String.valueOf(userId),
                            "/queue/chat/rooms",
                            ChatRoomUpdateEvent.builder()
                                    .chatRoomId(chatId)
                                    .lastMessage(saved.content())
                                    .lastMessageTime(
                                            ChatTimeFormatter.toDisplayTime(saved.createdAt())
                                    )
                                    .build()
                    );
                }
            }

        } catch (CustomException e) {
            messagingTemplate.convertAndSendToUser(
                    principal.getName(),
                    "/queue/errors",
                    new StompErrorResponse(
                            e.getStatusError().name(),
                            e.getMessage()
                    )
            );
        }
    }

}
