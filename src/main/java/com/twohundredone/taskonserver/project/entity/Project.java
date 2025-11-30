package com.twohundredone.taskonserver.project.entity;

import com.twohundredone.taskonserver.common.entity.BaseEntity;
import com.twohundredone.taskonserver.project.dto.ProjectCreateResponse;
import com.twohundredone.taskonserver.project.enums.Role;
import com.twohundredone.taskonserver.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Projects")
@Builder
public class Project extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "project_id")
    private Long projectId;

    @Column(name = "project_name", length = 50, nullable = false)
    private String projectName;

    @Column(name = "descripton")
    private String projectDescription;


    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)

    @Builder.Default
    List<ProjectMember> members = new ArrayList<>();

    public void addLeader(User user){
        ProjectMember leader = ProjectMember.builder()
                .project(this)
                .user(user)
                .role(Role.LEADER).build();
        this.members.add(leader);
    }
}
