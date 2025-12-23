package com.twohundredone.taskonserver.task.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.twohundredone.taskonserver.task.dto.TaskBoardItemDto;
import com.twohundredone.taskonserver.task.dto.TaskBoardItemDto.TaskBoardItemDtoBuilder;
import com.twohundredone.taskonserver.task.entity.QTask;
import com.twohundredone.taskonserver.task.entity.QTaskParticipant;
import com.twohundredone.taskonserver.task.entity.Task;
import com.twohundredone.taskonserver.task.entity.TaskParticipant;
import com.twohundredone.taskonserver.task.enums.TaskPriority;
import com.twohundredone.taskonserver.task.enums.TaskStatus;
import com.twohundredone.taskonserver.user.entity.QUser;
import com.twohundredone.taskonserver.user.entity.User;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
    public List<TaskBoardItemDto> findBoardItemsWithFilters(
            Long projectId,
            String title,
            TaskPriority priority,
            Long userId,
            boolean includeArchived
    ) {

        List<Tuple> rows = queryFactory
                .select(task, taskParticipant, user)
                .from(task)
                .leftJoin(taskParticipant).on(taskParticipant.task.eq(task))
                .leftJoin(taskParticipant.user, user)
                .where(
                        task.project.projectId.eq(projectId),
                        titleContains(title),
                        priorityEq(priority),
                        userCondition(userId),
                        notArchivedUnlessIncluded(includeArchived)
                )
                .orderBy(task.createdAt.desc())
                .fetch();

        return convertToBoardItems(rows);
    }

    private List<TaskBoardItemDto> convertToBoardItems(List<Tuple> rows) {

        Map<Long, TaskBoardItemDto.TaskBoardItemDtoBuilder> grouped = new LinkedHashMap<>();
        Map<Long, List<String>> participantImages = new LinkedHashMap<>();

        for (Tuple row : rows) {
            Task t = row.get(task);
            TaskParticipant tp = row.get(taskParticipant);
            User u = row.get(user);

            grouped.putIfAbsent(
                    t.getTaskId(),
                    TaskBoardItemDto.builder()
                            .taskId(t.getTaskId())
                            .title(t.getTaskTitle())
                            .status(t.getStatus())
                            .priority(t.getPriority())
                            .assigneeProfileImageUrl(null)
                            .participantProfileImageUrls(new ArrayList<>())
                            .commentCount(0)
            );

            participantImages.putIfAbsent(t.getTaskId(), new ArrayList<>());

            if (tp != null && u != null) {
                if (tp.getTaskRole().isAssignee()) {
                    grouped.get(t.getTaskId())
                            .assigneeProfileImageUrl(u.getProfileImageUrl());
                } else {
                    participantImages.get(t.getTaskId())
                            .add(u.getProfileImageUrl());
                }
            }
        }

        // 최종 build
        return grouped.entrySet().stream()
                .map(entry -> {
                    Long taskId = entry.getKey();
                    TaskBoardItemDtoBuilder builder = entry.getValue();
                    builder.participantProfileImageUrls(participantImages.get(taskId));
                    return builder.build();
                })
                .toList();
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

    private BooleanExpression notArchivedUnlessIncluded(boolean includeArchived) {
        return includeArchived ? null : task.status.ne(TaskStatus.ARCHIVED);
    }
}
