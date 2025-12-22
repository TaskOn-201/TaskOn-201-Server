package com.twohundredone.taskonserver.task.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.twohundredone.taskonserver.task.entity.QTaskParticipant;
import com.twohundredone.taskonserver.task.entity.Task;
import com.twohundredone.taskonserver.task.entity.TaskParticipant;
import com.twohundredone.taskonserver.task.enums.TaskRole;
import com.twohundredone.taskonserver.user.entity.User;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TaskParticipantQueryRepository {
    private final JPAQueryFactory queryFactory;

    public Optional<TaskParticipant> findNextAssignee(Task task, User excludeUser) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(QTaskParticipant.taskParticipant)
                        .where(
                                QTaskParticipant.taskParticipant.task.eq(task),
                                QTaskParticipant.taskParticipant.user.ne(excludeUser),
                                QTaskParticipant.taskParticipant.taskRole.eq(TaskRole.PARTICIPANT)
                        )
                        .orderBy(QTaskParticipant.taskParticipant.createdAt.asc())
                        .fetchFirst()
        );
    }
}
