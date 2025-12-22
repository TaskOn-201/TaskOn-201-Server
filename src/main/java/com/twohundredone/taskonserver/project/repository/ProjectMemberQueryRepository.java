package com.twohundredone.taskonserver.project.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.twohundredone.taskonserver.project.entity.Project;
import com.twohundredone.taskonserver.project.entity.ProjectMember;
import com.twohundredone.taskonserver.project.entity.QProjectMember;
import com.twohundredone.taskonserver.project.enums.Role;
import com.twohundredone.taskonserver.user.entity.User;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProjectMemberQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Optional<ProjectMember> findNextLeader(Project project, User excludeUser) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(QProjectMember.projectMember)
                        .where(
                                QProjectMember.projectMember.project.eq(project),
                                QProjectMember.projectMember.user.ne(excludeUser),
                                QProjectMember.projectMember.role.eq(Role.MEMBER)
                        )
                        .orderBy(QProjectMember.projectMember.createdAt.asc())
                        .fetchFirst()
        );
    }

}
