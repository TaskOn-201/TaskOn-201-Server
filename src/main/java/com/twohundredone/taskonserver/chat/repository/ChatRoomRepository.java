package com.twohundredone.taskonserver.chat.repository;

import com.twohundredone.taskonserver.chat.entity.ChatRoom;
import com.twohundredone.taskonserver.chat.enums.ChatType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    boolean existsByChatTypeAndProjectId(ChatType chatType, Long projectId);

    Optional<ChatRoom> findByChatTypeAndProjectId(ChatType chatType, Long projectId);

    boolean existsByChatTypeAndTaskId(ChatType chatType, Long taskId);

    Optional<ChatRoom> findByChatTypeAndTaskId(ChatType chatType, Long taskId);

    @Query("""
    select distinct cr
    from ChatRoom cr
    left join ProjectMember pm
        on pm.project.projectId = cr.projectId
       and pm.user.userId = :userId
    left join TaskParticipant tp
        on tp.task.taskId = cr.taskId
       and tp.user.userId = :userId
    left join ChatUser cu
        on cu.chatRoom = cr
       and cu.userId = :userId
    where
        (cr.chatType = 'PROJECT_GROUP' and pm.projectMemberId is not null)
     or (cr.chatType = 'TASK_GROUP' and tp.taskParticipantId is not null)
     or (cr.chatType = 'PERSONAL' and cu.chatUserId is not null)
    order by cr.modifiedAt desc
""")
    List<ChatRoom> findMyChatRooms(@Param("userId") Long userId);

    @Query("""
        select cr
        from ChatRoom cr
        join ChatUser cu1 on cu1.chatRoom = cr and cu1.userId = :userA
        join ChatUser cu2 on cu2.chatRoom = cr and cu2.userId = :userB
        where cr.chatType = :chatType
    """)
    Optional<ChatRoom> findPersonalChatRoom(
            Long userA,
            Long userB,
            ChatType chatType
    );
}
