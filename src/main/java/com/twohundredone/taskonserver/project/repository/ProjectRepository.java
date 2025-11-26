package com.twohundredone.taskonserver.project.repository;

import com.twohundredone.taskonserver.project.dto.ProjectSelectResponse;
import com.twohundredone.taskonserver.project.dto.TaskListResponse;
import com.twohundredone.taskonserver.project.entity.Project;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query("SELECT p, pm.role " +
            "FROM Project p JOIN ProjectMember pm ON p.projectId = pm.project.projectId " +
            "WHERE p.projectId = :projectId AND pm.user.userId = :userId")
    Optional<ProjectSelectResponse> findProjectWithMemberRole(@Param("projectId") Long projectId, @Param("userId") Long userId);

    List<Project> findAllByProjectId(Long projectId);
}
