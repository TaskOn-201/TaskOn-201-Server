package com.twohundredone.taskonserver.chat.controller;

import com.twohundredone.taskonserver.chat.dto.ChatMessageRequest;
import com.twohundredone.taskonserver.chat.dto.ChatMessageSendResponse;
import com.twohundredone.taskonserver.chat.service.ChatService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

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
        if (principal == null || principal.getName() == null) {
            // 인증 안된 연결이면 그냥 막기 (원하면 CustomException으로 바꿔도 됨)
            return;
        }

        Long senderUserId = Long.parseLong(principal.getName());

        ChatMessageSendResponse saved =
                chatService.saveMessage(chatId, senderUserId, request);

        messagingTemplate.convertAndSend(
                "/topic/chat/rooms/" + chatId,
                saved
        );
    }
}
