package com.twohundredone.taskonserver.chat.repository;

import com.twohundredone.taskonserver.chat.entity.ChatUser;
import java.util.Collection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatUserRepository extends JpaRepository<ChatUser, Long> {

    Optional<ChatUser> findByChatRoom_ChatIdAndUserId(Long chatId, Long userId);

    List<ChatUser> findAllByChatRoom_ChatId(Long chatId);
    List<ChatUser> findAllByChatRoom_ChatIdIn(Collection<Long> chatRoomIds);

    boolean existsByChatRoom_ChatIdAndUserId(Long chatId, Long userId);

    // 채팅방에서 특정 유저 제거
    void deleteByChatRoom_ChatIdAndUserId(Long chatId, Long userId);

    // 채팅방의 모든 유저 제거
    void deleteAllByChatRoom_ChatId(Long chatId);
}
