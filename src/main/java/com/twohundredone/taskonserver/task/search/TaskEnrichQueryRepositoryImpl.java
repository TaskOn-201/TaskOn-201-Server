package com.twohundredone.taskonserver.task.search;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.twohundredone.taskonserver.comment.entity.QComment;
import com.twohundredone.taskonserver.task.entity.QTaskParticipant;
import com.twohundredone.taskonserver.task.enums.TaskRole;
import com.twohundredone.taskonserver.task.search.dto.TaskCommentCountRow;
import com.twohundredone.taskonserver.task.search.dto.TaskParticipantEnrichRow;
import com.twohundredone.taskonserver.user.entity.QUser;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TaskEnrichQueryRepositoryImpl implements TaskEnrichQueryRepository {

    private final JPAQueryFactory queryFactory;

    QTaskParticipant tp = QTaskParticipant.taskParticipant;
    QUser u = QUser.user;
    QComment c = QComment.comment;

    @Override
    public List<TaskParticipantEnrichRow> findParticipantsByTaskIds(List<Long> taskIds) {
        return queryFactory
                .select(Projections.constructor(
                        TaskParticipantEnrichRow.class,
                        tp.task.taskId,
                        tp.taskRole.eq(TaskRole.ASSIGNEE),
                        u.profileImageUrl
                ))
                .from(tp)
                .join(tp.user, u)
                .where(tp.task.taskId.in(taskIds))
                .fetch();
    }

    @Override
    public List<TaskCommentCountRow> findCommentCountsByTaskIds(List<Long> taskIds) {
        return queryFactory
                .select(Projections.constructor(
                        TaskCommentCountRow.class,
                        c.task.taskId,
                        c.count()
                ))
                .from(c)
                .where(c.task.taskId.in(taskIds))
                .groupBy(c.task.taskId)
                .fetch();
    }
}

