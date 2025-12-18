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
import org.springframework.messaging.support.MessageBuilder;
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

        // 🔥 공통: principal 복구 (SEND / SUBSCRIBE 공통)
        if (accessor.getUser() == null && accessor.getSessionAttributes() != null) {
            Object saved = accessor.getSessionAttributes().get("user");
            if (saved instanceof Principal) {
                accessor.setUser((Principal) saved);
            }
        }

        if (StompCommand.CONNECT.equals(command)) {
            handleConnect(accessor);
        }
        else if (StompCommand.SUBSCRIBE.equals(command)) {
            handleSubscribe(accessor);
        }
        else if (StompCommand.SEND.equals(command)) {
            log.info("🔥 STOMP SEND intercepted destination={}", accessor.getDestination());
        }

        return MessageBuilder.createMessage(
                message.getPayload(),
                accessor.getMessageHeaders()
        );
    }

    private void handleConnect(StompHeaderAccessor accessor) {
        String bearer = getAuthorization(accessor);

        if (bearer == null || !bearer.startsWith("Bearer ")) {
            throw new MessagingException("WebSocket 인증 실패");
        }

        String token = bearer.substring("Bearer ".length());
        jwtProvider.validateToken(token);

        Long userId = jwtProvider.getUserId(token);

        Principal p = () -> String.valueOf(userId);
        accessor.setUser(p);

        // 세션에도 박아두면 다음 프레임에서 user가 null이어도 복구 가능
        if (accessor.getSessionAttributes() != null) {
            accessor.getSessionAttributes().put("user", p);
        }

        log.info("STOMP CONNECT success userId={}", userId);
    }

    private void handleSubscribe(StompHeaderAccessor accessor) {
        Principal principal = accessor.getUser();
        String destination = accessor.getDestination();

        // destination 없는 SUBSCRIBE는 STOMP 내부 처리용 → 무시
        if (destination == null || destination.isBlank()) return;

        if (principal == null && accessor.getSessionAttributes() != null) {
            Object saved = accessor.getSessionAttributes().get("user");
            if (saved instanceof Principal) {
                principal = (Principal) saved;
            }
        }

        if (principal == null) {
            throw new MessagingException("구독 인증 실패");
        }


        if (!destination.startsWith("/topic/chat/rooms/")) {
            return; // 다른 topic은 허용
        }

        Long userId = Long.parseLong(principal.getName());
        Long chatRoomId = extractChatRoomId(destination);

        chatService.validateChatRoomAccess(chatRoomId, userId);
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
