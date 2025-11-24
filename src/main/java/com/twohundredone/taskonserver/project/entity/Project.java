package com.twohundredone.taskonserver.project.entity;

import com.twohundredone.taskonserver.project.dto.ProjectCreateResponse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Projects")
@Builder
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "project_id")
    private Long projectId;

    @Column(name = "project_name", length = 50, nullable = false)
    private String projectName;

    @Column(name = "descripton")
    private String projectDescription;
}
