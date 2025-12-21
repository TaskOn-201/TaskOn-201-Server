package com.twohundredone.taskonserver.chat.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.twohundredone.taskonserver.chat.dto.ChatRoomSummaryDto;
import com.twohundredone.taskonserver.chat.entity.QChatMessage;
import com.twohundredone.taskonserver.chat.entity.QChatRoom;
import com.twohundredone.taskonserver.chat.entity.QChatUser;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChatRoomQueryRepositoryImpl implements ChatRoomQueryRepository {
    private final JPAQueryFactory queryFactory;

    QChatRoom chatRoom = QChatRoom.chatRoom;
    QChatUser chatUser = QChatUser.chatUser;

    @Override
    public List<ChatRoomSummaryDto> findMyChatRoomSummaries(Long userId) {
        QChatMessage lastMessage = new QChatMessage("lastMessage");
        QChatMessage unreadMessage = new QChatMessage("unreadMessage");

        return queryFactory
                .select(Projections.constructor(
                        ChatRoomSummaryDto.class,
                        chatRoom.chatId,            // chatRoomId
                        chatRoom.chatRoomName,      // roomName
                        chatRoom.chatType,          // chatType
                        lastMessage.content,        // lastMessage
                        lastMessage.createdAt,      // lastMessageAt (원본 시간)
                        unreadMessage.chatMessageId.count().intValue() // unreadCount
                ))
                .from(chatRoom)
                .join(chatUser).on(
                        chatUser.chatRoom.eq(chatRoom),
                        chatUser.userId.eq(userId)
                )
                // 마지막 메시지
                .leftJoin(lastMessage).on(
                        lastMessage.chatRoom.eq(chatRoom),
                        lastMessage.createdAt.eq(
                                JPAExpressions
                                        .select(lastMessage.createdAt.max())
                                        .from(lastMessage)
                                        .where(lastMessage.chatRoom.eq(chatRoom))
                        )
                )
                // 안 읽은 메시지
                .leftJoin(unreadMessage).on(
                        unreadMessage.chatRoom.eq(chatRoom),
                        unreadMessage.createdAt.after(chatUser.lastReadAt)
                )
                .groupBy(
                        chatRoom.chatId,
                        chatRoom.chatRoomName,
                        chatRoom.chatType,
                        lastMessage.content,
                        lastMessage.createdAt
                )
                .orderBy(chatRoom.modifiedAt.desc())
                .fetch();
    }
}
