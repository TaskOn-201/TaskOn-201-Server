package com.twohundredone.taskonserver.project.entity;

import com.twohundredone.taskonserver.common.entity.BaseEntity;
import com.twohundredone.taskonserver.project.enums.Role;
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
@Table(name="project_member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectMember extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_member_id")
    private Long projectMemberId;

    @Enumerated(EnumType.STRING)
    @Column(name = "project_role", nullable = false)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    private ProjectMember(Project project, User user, Role role) {
        this.project = project;
        this.user = user;
        this.role = role;
    }

    public static ProjectMember createMember(User user) {
        return ProjectMember.builder()
                .user(user)
                .role(Role.MEMBER)
                .build();
    }

    public static ProjectMember createLeader(Project project, User user) {
        return ProjectMember.builder()
                .project(project)
                .user(user)
                .role(Role.LEADER)
                .build();
    }

    // 연관관계 세팅용 (Project에서 호출)
    protected void setProject(Project project) {
        this.project = project;
    }
}
