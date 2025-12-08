package com.twohundredone.taskonserver.task.entity;

import com.twohundredone.taskonserver.task.enums.TaskRole;
import com.twohundredone.taskonserver.user.entity.User;
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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "task_participant")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TaskParticipant {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_participant_id")
    private Long taskParticipantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskRole taskRole;

    @Builder
    public TaskParticipant(Task task, User user, TaskRole taskRole) {
        this.task = task;
        this.user = user;
        this.taskRole = taskRole;
    }

    public void updateRole(TaskRole taskRole) {
        this.taskRole = taskRole;
    }
}
