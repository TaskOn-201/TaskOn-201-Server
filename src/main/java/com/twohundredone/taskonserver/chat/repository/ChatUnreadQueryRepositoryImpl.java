package com.twohundredone.taskonserver.chat.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.twohundredone.taskonserver.chat.entity.QChatMessage;
import com.twohundredone.taskonserver.chat.entity.QChatRoom;
import com.twohundredone.taskonserver.chat.entity.QChatUser;
import com.twohundredone.taskonserver.chat.enums.ChatType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChatUnreadQueryRepositoryImpl implements ChatUnreadQueryRepository {

    private final JPAQueryFactory queryFactory;

    QChatRoom chatRoom = QChatRoom.chatRoom;
    QChatUser chatUser = QChatUser.chatUser;
    QChatMessage chatMessage = QChatMessage.chatMessage;

    @Override
    public int countUnreadChatsInProject(Long projectId, Long userId) {

        Integer result = queryFactory
                .select(chatMessage.chatMessageId.count().intValue())
                .from(chatMessage)
                .join(chatRoom).on(chatMessage.chatRoom.eq(chatRoom))
                .join(chatUser).on(
                        chatUser.chatRoom.eq(chatRoom),
                        chatUser.userId.eq(userId)
                )
                .where(
                        chatMessage.createdAt.after(chatUser.lastReadAt),
                        chatRoom.chatType.in(ChatType.PROJECT_GROUP, ChatType.TASK_GROUP),
                        chatRoom.projectId.eq(projectId)
                )
                .fetchOne();

        return result != null ? result : 0;
    }
}

