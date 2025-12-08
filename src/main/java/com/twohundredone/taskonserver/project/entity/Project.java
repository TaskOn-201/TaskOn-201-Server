package com.twohundredone.taskonserver.project.entity;

import com.twohundredone.taskonserver.common.entity.BaseEntity;
import com.twohundredone.taskonserver.task.entity.Task;
import com.twohundredone.taskonserver.user.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "project")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Project extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long projectId;

    @Column(name = "project_name", length = 50, nullable = false)
    private String projectName;

    @Column(name = "descripton")
    private String projectDescription;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    List<ProjectMember> members = new ArrayList<>();

    @OneToMany(mappedBy = "project", orphanRemoval = false)
    @Builder.Default
    private List<Task> tasks = new ArrayList<>();

    public void addMember(ProjectMember member) {
        this.members.add(member);
        member.setProject(this);
    }

    public void addLeader(User user) {
        ProjectMember leader = ProjectMember.createLeader(this, user);
        addMember(leader);
    }
}
