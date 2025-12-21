package com.twohundredone.taskonserver.chat.config;

import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class StompWebsocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompHandler stompHandler;

    @Value("${frontend.url}")
    private String frontendUrls;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        String[] allowedOrigins = Arrays.stream(frontendUrls.split(","))
                .map(String::trim)
                .toArray(String[]::new);

        // 브라우저용 (SockJS)
        registry.addEndpoint("/ws/chat")
//                .setAllowedOriginPatterns(allowedOrigins)
                .setAllowedOriginPatterns("*")
                .withSockJS();

        // 테스트 / Postman / 순수 WebSocket용
        registry.addEndpoint("/ws/chat-ws")
                .setAllowedOriginPatterns(allowedOrigins);
//                .setAllowedOriginPatterns("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setUserDestinationPrefix("/user");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompHandler);
    }
}
