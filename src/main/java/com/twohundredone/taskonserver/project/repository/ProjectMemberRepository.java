package com.twohundredone.taskonserver.project.repository;

import com.twohundredone.taskonserver.project.entity.Project;
import com.twohundredone.taskonserver.project.entity.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    List<ProjectMember> findAllByUser_UserId(Long userId);
    List<ProjectMember> findAllByProject_ProjectId(Long projectId);
    Optional<ProjectMember> findByProject_ProjectIdAndUser_UserId(Long projectId, Long userId);

    Long project(Project project);

    boolean existsByProject_ProjectIdAndUser_UserId(Long projectId, Long userId);
}
