package com.twohundredone.taskonserver.project.repository;

import com.twohundredone.taskonserver.project.entity.Project;
import com.twohundredone.taskonserver.project.entity.ProjectMember;
import com.twohundredone.taskonserver.project.enums.Role;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    @Query("SELECT pm FROM ProjectMember pm JOIN FETCH pm.project p WHERE pm.user.userId = :userId")
    List<ProjectMember> findAllWithProjectByUserId(@Param("userId") Long userId);

    @Query("SELECT pm FROM ProjectMember pm JOIN FETCH pm.user u WHERE pm.project.projectId = :projectId")
    List<ProjectMember> findAllWithUserByProjectId(@Param("projectId") Long projectId);

    Optional<ProjectMember> findByProject_ProjectIdAndUser_UserId(Long projectId, Long userId);
    Long project(Project project);
    boolean existsByProject_ProjectIdAndUser_UserIdAndRole(Long projectId, Long userId, Role role);
    boolean existsByProject_ProjectIdAndUser_UserId(Long projectId, Long userId);
    List<ProjectMember> findByProject_ProjectIdAndUser_UserIdIn(Long projectId, List<Long> userIds);
}
