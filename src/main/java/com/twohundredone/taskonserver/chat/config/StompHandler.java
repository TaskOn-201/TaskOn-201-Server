package com.twohundredone.taskonserver.chat.config;

import com.twohundredone.taskonserver.auth.jwt.JwtProvider;
import com.twohundredone.taskonserver.chat.service.ChatService;
import com.twohundredone.taskonserver.global.enums.ResponseStatusError;
import com.twohundredone.taskonserver.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final JwtProvider jwtProvider;
    private final ChatService chatService;
    private static final String CHAT_ROOM_PREFIX = "/topic/chat/rooms/";

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();

        if (command == null) return message;

        // 1️⃣ CONNECT → JWT 인증
        if (StompCommand.CONNECT.equals(command)) {
            handleConnect(accessor);
        }

        // 2️⃣ SUBSCRIBE → 채팅방 권한 체크
        if (StompCommand.SUBSCRIBE.equals(command)) {
            handleSubscribe(accessor);
        }

        return message;
    }

    private void handleConnect(StompHeaderAccessor accessor) {
        String bearer = getAuthorization(accessor);

        if (bearer == null || !bearer.startsWith("Bearer ")) {
            throw new MessagingException("WebSocket 인증 실패");
        }

        String token = bearer.substring("Bearer ".length());
        jwtProvider.validateToken(token);

        Long userId = jwtProvider.getUserId(token);
        accessor.setUser(() -> String.valueOf(userId));

        log.info("STOMP CONNECT success userId={}", userId);
    }

    private void handleSubscribe(StompHeaderAccessor accessor) {
        Principal principal = accessor.getUser();
        String destination = accessor.getDestination();

        if (principal == null || destination == null) {
            throw new MessagingException("구독 인증 실패");
        }

        if (!destination.startsWith("/topic/chat/rooms/")) {
            return; // 다른 topic은 허용
        }

        Long userId = Long.parseLong(principal.getName());
        Long chatRoomId = extractChatRoomId(destination);

        try {
            chatService.validateChatRoomAccess(chatRoomId, userId);
        } catch (CustomException e) {
            throw new MessagingException("채팅방 구독 권한 없음");
        }
    }

    private String getAuthorization(StompHeaderAccessor accessor) {
        List<String> values = accessor.getNativeHeader("Authorization");
        return (values == null || values.isEmpty()) ? null : values.getFirst();
    }

    private Long extractChatRoomId(String destination) {
        if (destination == null || !destination.startsWith(CHAT_ROOM_PREFIX)) {
            throw new CustomException(ResponseStatusError.CHAT_BAD_REQUEST);
        }

        try {
            return Long.parseLong(destination.substring(CHAT_ROOM_PREFIX.length()));
        } catch (NumberFormatException e) {
            throw new CustomException(ResponseStatusError.CHAT_BAD_REQUEST);
        }
    }
}
