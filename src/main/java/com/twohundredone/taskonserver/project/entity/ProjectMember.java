package com.twohundredone.taskonserver.project.entity;

import com.twohundredone.taskonserver.project.enums.Role;
import com.twohundredone.taskonserver.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class ProjectMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_member_id")
    private Long projectMemeberId;

    @Column(nullable = false, name = "project_role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne
    @JoinColumn(name = "user_Id")
    private User user;
}
