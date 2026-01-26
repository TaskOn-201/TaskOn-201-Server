package com.twohundredone.taskonserver.task.service;

import com.twohundredone.taskonserver.task.dto.TaskBoardBaseRow;
import com.twohundredone.taskonserver.task.dto.TaskBoardItemDto;
import com.twohundredone.taskonserver.task.dto.TaskBoardResponse;
import com.twohundredone.taskonserver.task.dto.TaskParticipantRow;
import com.twohundredone.taskonserver.task.enums.TaskPriority;
import com.twohundredone.taskonserver.task.enums.TaskStatus;
import com.twohundredone.taskonserver.task.repository.TaskParticipantQueryRepository;
import com.twohundredone.taskonserver.task.repository.TaskQueryRepository;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskBoardQueryService {
    private final TaskQueryRepository taskQueryRepository;
    private final TaskParticipantQueryRepository taskParticipantQueryRepository;

    @Transactional(readOnly = true)
    @Cacheable(
            cacheNames = "taskBoard",
            key = "T(com.twohundredone.taskonserver.task.service.TaskCacheKeys).boardKey(#projectId, #title, #priority, #userId, #includeArchived)"
    )
    public TaskBoardResponse getTaskBoardCached(
            Long projectId,
            String title,
            TaskPriority priority,
            Long userId,
            boolean includeArchived
    ) {
        // Task 기본 조회
        List<TaskBoardBaseRow> tasks =
                taskQueryRepository.findTasksForBoard(projectId, title, priority, includeArchived);

        if (tasks.isEmpty()) {
            return TaskBoardResponse.builder()
                    .todo(List.of())
                    .inProgress(List.of())
                    .completed(List.of())
                    .archived(List.of())
                    .build();
        }

        List<Long> taskIds = tasks.stream()
                .map(TaskBoardBaseRow::taskId)
                .toList();

        // Participant 조회
        List<TaskParticipantRow> participants =
                taskParticipantQueryRepository.findParticipantsByTaskIds(taskIds);

        // Map으로 정리
        Map<Long, List<String>> participantMap = new LinkedHashMap<>();
        Map<Long, TaskBoardItemDto.TaskBoardItemDtoBuilder> taskMap = new LinkedHashMap<>();

        tasks.forEach(t -> {
            participantMap.put(t.taskId(), new ArrayList<>());

            taskMap.put(
                    t.taskId(),
                    TaskBoardItemDto.builder()
                            .taskId(t.taskId())
                            .title(t.title())
                            .status(t.status())
                            .priority(t.priority())
            );
        });


        // 참여자 매핑
        for (TaskParticipantRow p : participants) {
            TaskBoardItemDto.TaskBoardItemDtoBuilder builder = taskMap.get(p.taskId());
            if (builder == null) continue;

            if (p.assignee()) {
                builder.assigneeProfileImageUrl(p.profileImageUrl());
            } else {
                participantMap.get(p.taskId()).add(p.profileImageUrl());
            }
        }
        taskMap.forEach((taskId, builder) ->
                builder.participantProfileImageUrls(participantMap.get(taskId))
        );

        // 상태별 분리
        return TaskBoardResponse.builder()
                .todo(filter(taskMap, TaskStatus.TODO))
                .inProgress(filter(taskMap, TaskStatus.IN_PROGRESS))
                .completed(filter(taskMap, TaskStatus.COMPLETED))
                .archived(includeArchived ? filter(taskMap, TaskStatus.ARCHIVED) : null)
                .build();
    }

    private List<TaskBoardItemDto> filter(
            Map<Long, TaskBoardItemDto.TaskBoardItemDtoBuilder> map,
            TaskStatus status
    ) {
        return map.values().stream()
                .map(TaskBoardItemDto.TaskBoardItemDtoBuilder::build)
                .filter(dto -> dto.status() == status)
                .toList();
    }
}
