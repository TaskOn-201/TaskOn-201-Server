package com.twohundredone.taskonserver.chat.repository;

import com.twohundredone.taskonserver.chat.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // ✅ 메시지 목록 (오래된 → 최신)
    List<ChatMessage> findAllByChatRoom_ChatIdOrderByCreatedAtAsc(Long chatRoomId);

    // ✅ 마지막 메시지 (최신 1건)
    Optional<ChatMessage> findTop1ByChatRoom_ChatIdOrderByCreatedAtDesc(Long chatRoomId);

    // ✅ 안읽은 메시지 개수 (lastReadAt 이후)
    int countByChatRoom_ChatIdAndCreatedAtAfter(Long chatRoomId, LocalDateTime lastReadAt);
}
