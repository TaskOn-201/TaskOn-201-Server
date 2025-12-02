package com.twohundredone.taskonserver.task.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.twohundredone.taskonserver.project.entity.QProjectMember;
import com.twohundredone.taskonserver.task.dto.TaskUserSearchResponse;
import com.twohundredone.taskonserver.user.entity.QUser;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TaskUserQueryRepositoryImpl implements TaskUserQueryRepository {

    private final JPAQueryFactory queryFactory;

    QUser user = QUser.user;
    QProjectMember projectMember = QProjectMember.projectMember;


    @Override
    public Slice<TaskUserSearchResponse> searchProjectMembersForTask(
            Long projectId,
            String keyword,
            Pageable pageable
    ) {
        int pageSize = pageable.getPageSize();

        List<TaskUserSearchResponse> result = queryFactory
                .select(Projections.constructor(
                        TaskUserSearchResponse.class,
                        user.userId,
                        user.name,
                        user.email,
                        user.profileImageUrl
                ))
                .from(projectMember)
                .join(projectMember.user, user)
                .where(
                        projectMember.project.projectId.eq(projectId),
                        contains(keyword)
                )
                .orderBy(user.name.asc(), user.email.asc())
                .offset(pageable.getOffset())
                .limit(pageSize + 1)  // Slice: next 여부 판단
                .fetch();

        boolean hasNext = false;
        if (result.size() > pageSize) {
            hasNext = true;
            result.remove(result.size() - 1);
        }

        return new SliceImpl<>(result, pageable, hasNext);
    }

    // 조건 함수들
    private BooleanExpression contains(String keyword) {
        if (keyword == null || keyword.isBlank()) return null;

        String trimmed = keyword.trim();
        if (trimmed.isBlank()) return null;

        return user.name.containsIgnoreCase(trimmed)
                .or(user.email.containsIgnoreCase(trimmed));
    }
}
