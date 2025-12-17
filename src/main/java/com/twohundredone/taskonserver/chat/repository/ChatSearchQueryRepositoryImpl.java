package com.twohundredone.taskonserver.chat.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.twohundredone.taskonserver.chat.dto.ChatSearchResponse;
import com.twohundredone.taskonserver.chat.dto.ChatSearchResponse.UserSummary;
import com.twohundredone.taskonserver.project.entity.QProjectMember;
import com.twohundredone.taskonserver.task.entity.QTask;
import com.twohundredone.taskonserver.task.entity.QTaskParticipant;
import com.twohundredone.taskonserver.user.entity.QUser;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChatSearchQueryRepositoryImpl implements ChatSearchQueryRepository {

    private final JPAQueryFactory queryFactory;

    private static final int SEARCH_LIMIT = 10;

    QUser user = QUser.user;
    QTask task = QTask.task;
    QProjectMember pm = QProjectMember.projectMember;
    QTaskParticipant tp = QTaskParticipant.taskParticipant;

    // 사용자 검색
    @Override
    public List<UserSummary> searchUsers(Long userId, String keyword) {
        return queryFactory
                .select(Projections.constructor(
                        ChatSearchResponse.UserSummary.class,
                        user.userId,
                        user.name,
                        user.profileImageUrl
                ))
                .from(user)
                .where(
                        containsKeyword(user.name, keyword),
                        user.userId.ne(userId),
                        existsRelation(userId)
                )
                .limit(SEARCH_LIMIT)
                .fetch();
    }

    // 업무 검색
    @Override
    public List<ChatSearchResponse.TaskSummary> searchTasks(Long userId, String keyword) {
        return queryFactory
                .select(Projections.constructor(
                        ChatSearchResponse.TaskSummary.class,
                        task.taskId,
                        task.project.projectId,
                        task.taskTitle,
                        task.priority.stringValue()
                ))
                .from(task)
                .where(
                        containsKeyword(task.taskTitle, keyword),
                        taskAccessibleBy(userId)
                )
                .limit(SEARCH_LIMIT)
                .fetch();
    }

    // 접근 권한 필터
    private BooleanExpression taskAccessibleBy(Long userId) {
        return JPAExpressions
                .selectOne()
                .from(tp)
                .where(
                        tp.task.eq(task),
                        tp.user.userId.eq(userId)
                )
                .exists()
                .or(
                        JPAExpressions
                                .selectOne()
                                .from(pm)
                                .where(
                                        pm.project.eq(task.project),
                                        pm.user.userId.eq(userId)
                                )
                                .exists()
                );
    }

    // 사용자 관계 필터
    private BooleanExpression existsRelation(Long userId) {

        QProjectMember pmMe = new QProjectMember("pmMe");
        QProjectMember pmTarget = new QProjectMember("pmTarget");

        QTaskParticipant tpMe = new QTaskParticipant("tpMe");
        QTaskParticipant tpTarget = new QTaskParticipant("tpTarget");

        return JPAExpressions
                .selectOne()
                .from(pmMe)
                .join(pmTarget)
                .on(pmMe.project.eq(pmTarget.project))
                .where(
                        pmMe.user.userId.eq(userId),
                        pmTarget.user.userId.eq(user.userId)
                )
                .exists()
                .or(
                        JPAExpressions
                                .selectOne()
                                .from(tpMe)
                                .join(tpTarget)
                                .on(tpMe.task.eq(tpTarget.task))
                                .where(
                                        tpMe.user.userId.eq(userId),
                                        tpTarget.user.userId.eq(user.userId)
                                )
                                .exists()
                );
    }

    // 공통 키워드 필터
    private BooleanExpression containsKeyword(StringPath path, String keyword) {
        return (keyword == null || keyword.isBlank())
                ? null
                : path.containsIgnoreCase(keyword);
    }
}