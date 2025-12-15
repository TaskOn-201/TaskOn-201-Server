package com.twohundredone.taskonserver.chat.config;

import com.twohundredone.taskonserver.auth.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final JwtProvider jwtProvider;

    @Override
    @NonNull
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (accessor.getCommand() == null) return message;

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String bearer = getAuthorization(accessor);

            if (bearer == null || !bearer.startsWith("Bearer ")) {
                throw new MessagingException("WebSocket 인증 실패: Authorization 누락/형식 오류");
            }

            String token = bearer.substring("Bearer ".length()).trim();
            if (token.isEmpty()) throw new MessagingException("WebSocket 인증 실패: 토큰 비어있음");

            try {
                //  검증 + userId 추출 (REST와 동일 로직)
                jwtProvider.validateToken(token);
                Long userId = jwtProvider.getUserId(token);

                // (선택) 세션 저장
                Map<String, Object> session = accessor.getSessionAttributes();
                if (session != null) {
                    session.put("accessToken", token);
                    session.put("userId", userId);
                }

                // 핵심: Principal 세팅 → Controller에서 principal.getName() == userId
                accessor.setUser(new StompPrincipal(String.valueOf(userId)));

                log.info("STOMP CONNECT 인증 성공 userId={}", userId);

            } catch (Exception e) {
                throw new MessagingException("WebSocket 인증 실패: 유효하지 않은 토큰");
            }
        }

        return message;
    }

    private String getAuthorization(StompHeaderAccessor accessor) {
        List<String> values = accessor.getNativeHeader("Authorization");
        if (values == null || values.isEmpty()) return null;
        return values.getFirst();
    }

    private record StompPrincipal(String name) implements Principal {

        @Override
        public String getName() {
            return name;
        }
    }
}
