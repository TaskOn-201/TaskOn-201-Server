package com.twohundredone.taskonserver.task.search;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.twohundredone.taskonserver.task.dto.TaskBoardItemDto;
import com.twohundredone.taskonserver.task.enums.TaskPriority;
import com.twohundredone.taskonserver.task.enums.TaskStatus;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TaskSearchQueryRepository {
    private final ElasticsearchOperations operations;

    public Page<TaskBoardItemDto> search(
            Long projectId,
            String keyword,
            TaskPriority priority,
            TaskStatus status,
            Long assigneeId,
            int page,
            int size
    ) {
        // 1️⃣ BoolQuery 생성 (Boot 3.x 방식)
        Query query = BoolQuery.of(b -> {
            // projectId는 반드시 필터
            b.filter(f -> f.term(t -> t.field("projectId").value(projectId)));

            if (keyword != null && !keyword.isBlank()) {
                b.must(m -> m.match(mm -> mm.field("title").query(keyword)));
            }
            if (priority != null) {
                b.filter(f -> f.term(t -> t.field("priority").value(priority.name())));
            }
            if (status != null) {
                b.filter(f -> f.term(t -> t.field("status").value(status.name())));
            }
            if (assigneeId != null) {
                b.filter(f -> f.term(t -> t.field("assigneeId").value(assigneeId)));
            }
            return b;
        })._toQuery();

        Pageable pageable =
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        // 2️⃣ NativeQuery (Boot 3.x 전용)
        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(query)
                .withPageable(pageable)
                .build();

        // 3️⃣ 실행
        SearchHits<TaskSearchDocument> hits =
                operations.search(searchQuery, TaskSearchDocument.class);

        List<TaskBoardItemDto> content = hits.getSearchHits().stream()
                .map(hit -> toBoardItem(hit.getContent()))
                .toList();

        return new PageImpl<>(content, pageable, hits.getTotalHits());
    }

    private TaskBoardItemDto toBoardItem(TaskSearchDocument d) {
        return TaskBoardItemDto.builder()
                .taskId(d.getTaskId())
                .title(d.getTitle())
                .status(TaskStatus.valueOf(d.getStatus()))
                .priority(TaskPriority.valueOf(d.getPriority()))
                .build();
    }
}
