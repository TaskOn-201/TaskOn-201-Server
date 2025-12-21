package com.twohundredone.taskonserver.chat.repository;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.twohundredone.taskonserver.chat.dto.ChatParticipantFlatDto;
import com.twohundredone.taskonserver.chat.dto.ChatSearchResponse.ChatRoomSearchItem;
import com.twohundredone.taskonserver.chat.entity.QChatMessage;
import com.twohundredone.taskonserver.chat.entity.QChatRoom;
import com.twohundredone.taskonserver.chat.entity.QChatUser;
import com.twohundredone.taskonserver.task.entity.QTask;
import com.twohundredone.taskonserver.user.entity.QUser;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChatSearchQueryRepositoryImpl implements ChatSearchQueryRepository {

    private final JPAQueryFactory queryFactory;

    private static final int SEARCH_LIMIT = 10;

    QChatRoom chatRoom = QChatRoom.chatRoom;
    QChatUser chatUser = QChatUser.chatUser;
    QChatUser otherChatUser = new QChatUser("otherChatUser");
    QChatMessage chatMessage = QChatMessage.chatMessage;
    QChatMessage lastMessage = new QChatMessage("lastMessage");
    QUser user = QUser.user;
    QTask task = QTask.task;


    // 채팅 검색
    @Override
    public List<ChatRoomSearchItem> searchChatRooms(Long userId, String keyword) {

        return baseChatRoomQuery(userId)
                .leftJoin(otherChatUser).on(otherChatUser.chatRoom.eq(chatRoom))
                .leftJoin(user).on(user.userId.eq(otherChatUser.userId))
                .leftJoin(task).on(task.taskId.eq(chatRoom.taskId))
                .where(searchCondition(userId, keyword))
                .distinct()
                .orderBy(lastMessage.createdAt.desc().nullsLast())
                .limit(SEARCH_LIMIT)
                .fetch();
    }

    @Override
    public List<ChatParticipantFlatDto> findParticipantsByChatRoomIds(
            List<Long> chatRoomIds
    ) {
        return queryFactory
                .select(Projections.constructor(
                        ChatParticipantFlatDto.class,
                        chatUser.chatRoom.chatId,
                        user.userId,
                        user.name,
                        user.profileImageUrl
                ))
                .from(chatUser)
                .join(user).on(user.userId.eq(chatUser.userId))
                .where(chatUser.chatRoom.chatId.in(chatRoomIds))
                .fetch();
    }


    // 공통 베이스 쿼리
    private JPAQuery<ChatRoomSearchItem> baseChatRoomQuery(Long userId) {

        return queryFactory
                .select(Projections.constructor(
                        ChatRoomSearchItem.class,
                        chatRoom.chatId,
                        chatRoom.chatRoomName,
                        chatRoom.chatType,
                        chatRoom.taskId,
                        lastMessage.content,
                        lastMessage.createdAt,
                        unreadCountSubQuery(userId)
                ))
                .from(chatRoom)
                .join(chatUser).on(chatUser.chatRoom.eq(chatRoom))
                .leftJoin(lastMessage).on(
                        lastMessage.chatRoom.eq(chatRoom),
                        lastMessage.createdAt.eq(
                                JPAExpressions
                                        .select(chatMessage.createdAt.max())
                                        .from(chatMessage)
                                        .where(chatMessage.chatRoom.eq(chatRoom))
                        )
                )
                .where(chatUser.userId.eq(userId));
    }

    // 검색 조건

    private BooleanExpression searchCondition(Long userId, String keyword) {
        if (keyword == null || keyword.isBlank()) return null;

        return user.name.containsIgnoreCase(keyword)
                .and(otherChatUser.userId.ne(userId))
                .or(
                        task.taskTitle.containsIgnoreCase(keyword)
                );
    }

    // 안 읽은 메시지 수 (lastReadAt 기준)
    private Expression<Integer> unreadCountSubQuery(Long userId) {

        QChatUser me = new QChatUser("me");

        return JPAExpressions
                .select(chatMessage.count().intValue())
                .from(chatMessage)
                .join(me).on(
                        me.chatRoom.eq(chatRoom),
                        me.userId.eq(userId)
                )
                .where(
                        chatMessage.chatRoom.eq(chatRoom),
                        chatMessage.createdAt.after(me.lastReadAt)
                );
    }
}
