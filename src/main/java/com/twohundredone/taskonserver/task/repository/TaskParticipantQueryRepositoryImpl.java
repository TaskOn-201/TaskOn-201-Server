package com.twohundredone.taskonserver.task.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.twohundredone.taskonserver.task.dto.TaskParticipantRow;
import com.twohundredone.taskonserver.task.entity.QTaskParticipant;
import com.twohundredone.taskonserver.task.entity.Task;
import com.twohundredone.taskonserver.task.entity.TaskParticipant;
import com.twohundredone.taskonserver.task.enums.TaskRole;
import com.twohundredone.taskonserver.user.entity.QUser;
import com.twohundredone.taskonserver.user.entity.User;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TaskParticipantQueryRepositoryImpl
        implements TaskParticipantQueryRepository {

    private final JPAQueryFactory queryFactory;

    QTaskParticipant taskParticipant = QTaskParticipant.taskParticipant;
    QUser user = QUser.user;

    @Override
    public Optional<TaskParticipant> findNextAssignee(Task task, User excludeUser) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(taskParticipant)
                        .where(
                                taskParticipant.task.eq(task),
                                taskParticipant.user.ne(excludeUser),
                                taskParticipant.taskRole.eq(TaskRole.PARTICIPANT)
                        )
                        .orderBy(taskParticipant.createdAt.asc())
                        .fetchFirst()
        );
    }

    @Override
    public List<TaskParticipantRow> findParticipantsByTaskIds(List<Long> taskIds) {
        return queryFactory
                .select(
                        Projections.constructor(
                                TaskParticipantRow.class,
                                taskParticipant.task.taskId,
                                user.userId,
                                user.profileImageUrl,
                                taskParticipant.taskRole.eq(TaskRole.ASSIGNEE)
                        )
                )
                .from(taskParticipant)
                .join(taskParticipant.user, user)
                .where(taskParticipant.task.taskId.in(taskIds))
                .fetch();
    }
}
