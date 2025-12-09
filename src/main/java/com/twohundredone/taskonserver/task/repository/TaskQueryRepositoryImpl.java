package com.twohundredone.taskonserver.task.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.twohundredone.taskonserver.task.entity.QTask;
import com.twohundredone.taskonserver.task.entity.QTaskParticipant;
import com.twohundredone.taskonserver.task.entity.Task;
import com.twohundredone.taskonserver.task.enums.TaskPriority;
import com.twohundredone.taskonserver.user.entity.QUser;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TaskQueryRepositoryImpl implements TaskQueryRepository {
    private final JPAQueryFactory queryFactory;

    QTask task = QTask.task;
    QTaskParticipant taskParticipant = QTaskParticipant.taskParticipant;
    QUser user = QUser.user;

    @Override
    public List<Task> findTasksWithFilters(
            Long projectId,
            String title,
            TaskPriority priority,
            Long userId
    ) {

        return queryFactory
                .select(task)
                .distinct()
                .from(task)
                .leftJoin(taskParticipant)
                .on(taskParticipant.task.eq(task))
                .leftJoin(taskParticipant.user, user)
                .where(
                        task.project.projectId.eq(projectId),
                        titleContains(title),
                        priorityEq(priority),
                        userCondition(userId)
                )
                .orderBy(task.createdAt.desc())
                .fetch();
    }

    private BooleanExpression titleContains(String title) {
        return title != null ? task.taskTitle.containsIgnoreCase(title) : null;
    }

    private BooleanExpression priorityEq(TaskPriority priority) {
        return priority != null ? task.priority.eq(priority) : null;
    }

    // userId로 검색하면 해당 유저가 assignee 또는 participant로 포함된 task만
    private BooleanExpression userCondition(Long userId) {
        return userId != null
                ? taskParticipant.user.userId.eq(userId)
                : null;
    }
}
