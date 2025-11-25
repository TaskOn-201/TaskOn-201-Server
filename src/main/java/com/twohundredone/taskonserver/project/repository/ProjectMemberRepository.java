package com.twohundredone.taskonserver.project.repository;

import com.twohundredone.taskonserver.project.entity.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
}
