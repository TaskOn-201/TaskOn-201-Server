package com.twohundredone.taskonserver.chat.repository;

import com.twohundredone.taskonserver.chat.entity.ChatUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatUserRepository extends JpaRepository<ChatUser, Long> {

    Optional<ChatUser> findByChatIdAndUserId(Long chatId, Long userId);

    List<ChatUser> findAllByChatId(Long chatId);
}
