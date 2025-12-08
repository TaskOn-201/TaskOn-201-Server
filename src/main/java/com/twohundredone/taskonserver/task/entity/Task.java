package com.twohundredone.taskonserver.task.entity;

import com.twohundredone.taskonserver.common.entity.BaseEntity;
import com.twohundredone.taskonserver.project.entity.Project;
import com.twohundredone.taskonserver.task.enums.TaskPriority;
import com.twohundredone.taskonserver.task.enums.TaskStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "task")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Task extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private Long taskId;

    // Project 연결 (LAZY 지연로딩)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "task_title", nullable = false)
    private String taskTitle;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskPriority priority;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Builder
    public Task(
            Project project,
            String taskTitle,
            String description,
            TaskStatus status,
            TaskPriority priority,
            LocalDate startDate,
            LocalDate dueDate
    ) {
        this.project = project;
        this.taskTitle = taskTitle;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.startDate = startDate;
        this.dueDate = dueDate;
    }

    public void updateTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateStatus(TaskStatus status) {
        if (status != null) {
            this.status = status;
        }
    }

    public void updatePriority(TaskPriority priority) {
        if (priority != null) {
            this.priority = priority;
        }
    }

    public void updateDates(LocalDate startDate, LocalDate dueDate) {
        this.startDate = startDate;
        this.dueDate = dueDate;
    }
}