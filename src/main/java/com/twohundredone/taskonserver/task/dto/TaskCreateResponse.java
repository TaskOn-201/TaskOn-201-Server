package com.twohundredone.taskonserver.task.dto;

import com.twohundredone.taskonserver.task.entity.Task;
import com.twohundredone.taskonserver.task.enums.TaskPriority;
import com.twohundredone.taskonserver.task.enums.TaskStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class TaskCreateResponse {

    private Long taskId;
    private Long projectId;
    private Long userId;
    private String taskTitle;
    private String description;
    private LocalDate dueDate;
    private TaskStatus status;
    private TaskPriority priority;


    public static TaskCreateResponse from(Task task) {
        return TaskCreateResponse.builder()
                .taskId(task.getTaskId())
                .projectId(task.getProject().getProjectId())
                .taskTitle(task.getTaskTitle())
                .description(task.getDescription())
                .dueDate(task.getDueDate())
                .status(task.getStatus())
                .priority(task.getPriority())
                .build();
    }
}

