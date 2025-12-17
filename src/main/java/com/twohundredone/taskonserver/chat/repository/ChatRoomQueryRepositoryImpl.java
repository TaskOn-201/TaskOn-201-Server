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
    QChatMessage chatMessage = QChatMessage.chatMessage;

    @Override
    public List<ChatRoomSummaryDto> findMyChatRoomSummaries(Long userId) {

        // 서브쿼리: 마지막 메시지 시간
        QChatMessage subMessage = new QChatMessage("subMessage");

        return queryFactory
                .select(Projections.constructor(
                        ChatRoomSummaryDto.class,
                        chatRoom.chatId,
                        chatRoom.chatRoomName,
                        chatMessage.content,
                        chatMessage.createdAt,
                        chatMessage.chatMessageId.count().intValue()
                ))
                .from(chatRoom)
                // 내 채팅방만
                .join(chatUser).on(
                        chatUser.chatRoom.eq(chatRoom),
                        chatUser.userId.eq(userId)
                )
                // 마지막 메시지
                .leftJoin(chatMessage).on(
                        chatMessage.chatRoom.eq(chatRoom),
                        chatMessage.createdAt.eq(
                                JPAExpressions
                                        .select(subMessage.createdAt.max())
                                        .from(subMessage)
                                        .where(subMessage.chatRoom.eq(chatRoom))
                        )
                )
                // 안 읽은 메시지
                .leftJoin(chatMessage).on(
                        chatMessage.chatRoom.eq(chatRoom),
                        chatMessage.createdAt.after(chatUser.lastReadAt)
                )
                .groupBy(
                        chatRoom.chatId,
                        chatRoom.chatRoomName,
                        chatMessage.content,
                        chatMessage.createdAt
                )
                .orderBy(chatRoom.modifiedAt.desc())
                .fetch();
    }
}
