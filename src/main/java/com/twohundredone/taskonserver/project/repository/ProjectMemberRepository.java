package com.twohundredone.taskonserver.project.repository;

import com.twohundredone.taskonserver.project.dto.TaskListResponse;
import com.twohundredone.taskonserver.project.entity.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
}
