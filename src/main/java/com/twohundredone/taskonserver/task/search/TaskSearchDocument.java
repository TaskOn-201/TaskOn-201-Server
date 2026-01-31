package com.twohundredone.taskonserver.task.search;

import com.twohundredone.taskonserver.task.entity.Task;
import org.springframework.data.annotation.Id;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(indexName = "task_search")
public class TaskSearchDocument {
    @Id
    private Long taskId;

    @Field(type = FieldType.Long)
    private Long projectId;

    @Field(type = FieldType.Text) // 검색용
    private String title;

    @Field(type = FieldType.Keyword) // 필터용
    private String status;

    @Field(type = FieldType.Keyword)
    private String priority;

    @Field(type = FieldType.Long)
    private Long assigneeId;

    @Field(type = FieldType.Date)
    private LocalDateTime createdAt;

    public static TaskSearchDocument from(
            Task task,
            Long assigneeId
    ) {
        return TaskSearchDocument.builder()
                .taskId(task.getTaskId())
                .projectId(task.getProject().getProjectId())
                .title(task.getTaskTitle())
                .status(task.getStatus().name())
                .priority(task.getPriority().name())
                .assigneeId(assigneeId)
                .createdAt(task.getCreatedAt())
                .build();
    }

}
