package com.twohundredone.taskonserver.chat.repository;

import com.twohundredone.taskonserver.chat.entity.ChatRoom;
import com.twohundredone.taskonserver.chat.enums.ChatType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByProjectIdAndChatType(Long projectId, ChatType chatType);

    Optional<ChatRoom> findByTaskIdAndChatType(Long taskId, ChatType chatType);


    // ✅ 내 채팅방 목록 (PROJECT + TASK + PERSONAL)
    @Query(value = """
        SELECT *
        FROM (
            SELECT cr.*
            FROM chat_room cr
            JOIN project_member pm
              ON pm.project_id = cr.project_id
            WHERE pm.user_id = :userId
              AND cr.chat_type = 'PROJECT_GROUP'

            UNION

            SELECT cr.*
            FROM chat_room cr
            JOIN task_participant tp
              ON tp.task_id = cr.task_id
            WHERE tp.user_id = :userId
              AND cr.chat_type = 'TASK_GROUP'

            UNION

            SELECT cr.*
            FROM chat_room cr
            JOIN chat_user cu
              ON cu.chat_id = cr.chat_id
            WHERE cu.user_id = :userId
              AND cr.chat_type = 'PERSONAL'
        ) t
        ORDER BY t.updated_at DESC
    """, nativeQuery = true)
    List<ChatRoom> findMyChatRooms(@Param("userId") Long userId);

    // ✅ PERSONAL 채팅방 중복 방 조회 (JPQL)
    @Query("""
        select cr
        from ChatRoom cr
        join ChatUser cu1 on cu1.chatId = cr.chatId and cu1.userId = :userA
        join ChatUser cu2 on cu2.chatId = cr.chatId and cu2.userId = :userB
        where cr.chatType = :chatType
    """)
    Optional<ChatRoom> findPersonalChatRoom(
            @Param("userA") Long userA,
            @Param("userB") Long userB,
            @Param("chatType") ChatType chatType
    );
}
