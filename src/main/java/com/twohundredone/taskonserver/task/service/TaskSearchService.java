package com.twohundredone.taskonserver.task.service;

import static com.twohundredone.taskonserver.global.enums.ResponseStatusError.PROJECT_FORBIDDEN;

import com.twohundredone.taskonserver.global.exception.CustomException;
import com.twohundredone.taskonserver.project.repository.ProjectMemberRepository;
import com.twohundredone.taskonserver.task.dto.TaskBoardItemDto;
import com.twohundredone.taskonserver.task.enums.TaskPriority;
import com.twohundredone.taskonserver.task.enums.TaskStatus;
import com.twohundredone.taskonserver.task.search.TaskEnrichQueryRepository;
import com.twohundredone.taskonserver.task.search.TaskSearchQueryRepository;
import com.twohundredone.taskonserver.task.search.dto.TaskCommentCountRow;
import com.twohundredone.taskonserver.task.search.dto.TaskParticipantEnrichRow;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskSearchService {
    private final ProjectMemberRepository projectMemberRepository;
    private final TaskSearchQueryRepository taskSearchQueryRepository;
    private final TaskEnrichQueryRepository taskEnrichQueryRepository;

    public Page<TaskBoardItemDto> search(
            Long loginUserId,
            Long projectId,
            String keyword,
            TaskPriority priority,
            TaskStatus status,
            Long assigneeId,
            int page,
            int size
    ) {
        // 권한 체크
        projectMemberRepository
                .findByProject_ProjectIdAndUser_UserId(projectId, loginUserId)
                .orElseThrow(() -> new CustomException(PROJECT_FORBIDDEN));

        // ES 검색 (taskId, title, status, priority만 있음)
        Page<TaskBoardItemDto> searchPage =
                taskSearchQueryRepository.search(
                        projectId,
                        keyword,
                        priority,
                        status,
                        assigneeId,
                        page,
                        size
                );

        if (searchPage.isEmpty()) {
            return searchPage;
        }

        // 3️⃣ taskId 수집
        List<Long> taskIds = searchPage.getContent().stream()
                .map(TaskBoardItemDto::taskId)
                .toList();

        // DB Enrich 조회
        List<TaskParticipantEnrichRow> participantRows =
                taskEnrichQueryRepository.findParticipantsByTaskIds(taskIds);

        List<TaskCommentCountRow> commentCountRows =
                taskEnrichQueryRepository.findCommentCountsByTaskIds(taskIds);

        // 참여자/담당자 Map 구성
        Map<Long, String> assigneeImageMap = new HashMap<>();
        Map<Long, List<String>> participantImageMap = new HashMap<>();

        for (TaskParticipantEnrichRow row : participantRows) {
            participantImageMap.putIfAbsent(row.taskId(), new ArrayList<>());

            if (row.assignee()) {
                assigneeImageMap.put(row.taskId(), row.profileImageUrl());
            } else {
                participantImageMap
                        .get(row.taskId())
                        .add(row.profileImageUrl());
            }
        }

        // 댓글 수 Map
        Map<Long, Integer> commentCountMap = new HashMap<>();
        for (TaskCommentCountRow row : commentCountRows) {
            commentCountMap.put(row.taskId(), (int) row.commentCount());
        }

        // 최종 DTO 재조립
        List<TaskBoardItemDto> enrichedContent =
                searchPage.getContent().stream()
                        .map(dto ->
                                TaskBoardItemDto.builder()
                                        .taskId(dto.taskId())
                                        .title(dto.title())
                                        .status(dto.status())
                                        .priority(dto.priority())
                                        .assigneeProfileImageUrl(
                                                assigneeImageMap.get(dto.taskId())
                                        )
                                        .participantProfileImageUrls(
                                                participantImageMap.getOrDefault(
                                                        dto.taskId(),
                                                        List.of()
                                                )
                                        )
                                        .commentCount(
                                                commentCountMap.getOrDefault(
                                                        dto.taskId(),
                                                        0
                                                )
                                        )
                                        .build()
                        )
                        .toList();

        // Page 유지해서 반환
        return new PageImpl<>(
                enrichedContent,
                searchPage.getPageable(),
                searchPage.getTotalElements()
        );
    }
}
