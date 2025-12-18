package com.twohundredone.taskonserver.user.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.twohundredone.taskonserver.project.entity.QProjectMember;
import com.twohundredone.taskonserver.user.dto.UserSearchResponse;
import com.twohundredone.taskonserver.user.entity.QUser;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserQueryRepositoryImpl implements UserQueryRepository {

    private final JPAQueryFactory queryFactory;

    QUser user = QUser.user;
    QProjectMember projectMember = QProjectMember.projectMember;

    @Override
    public Slice<UserSearchResponse> searchUsers(Long loginUserId, Long projectId, String keyword, Pageable pageable) {

        int pageSize = pageable.getPageSize();

        List<UserSearchResponse> result = queryFactory
                .select(Projections.constructor(
                        UserSearchResponse.class,
                        user.userId,
                        user.name,
                        user.email,
                        user.profileImageUrl
                ))
                .from(user)
                .where(
                        contains(keyword),
                        notSelf(loginUserId),
                        notProjectMember(projectId)
                )
                .orderBy(user.name.asc(), user.email.asc())
                .offset(pageable.getOffset())
                // pageSize + 1 로 조회 → hasNext 판단용
                .limit(pageSize + 1)
                .fetch();

        boolean hasNext = false;
        if (result.size() > pageSize) {
            hasNext = true;
            result.remove(result.size() - 1); // 마지막 데이터 제거
        }

        return new SliceImpl<>(result, pageable, hasNext);
    }


    // 선택된 사용자 조회
    @Override
    public List<UserSearchResponse> findUsersByIds(List<Long> userIds) {
        return queryFactory
                .select(Projections.constructor(
                        UserSearchResponse.class,
                        user.userId,
                        user.name,
                        user.email,
                        user.profileImageUrl
                ))
                .from(user)
                .where(user.userId.in(userIds))
                .orderBy(user.name.asc(), user.email.asc())
                .fetch();
    }

    // 조건 함수들
    private BooleanExpression contains(String keyword) {
        if (keyword == null || keyword.isBlank()) return null;

        String trimmed = keyword.trim();
        if (trimmed.isBlank()) return null;

        return user.name.containsIgnoreCase(trimmed)
                .or(user.email.containsIgnoreCase(trimmed));
    }

    private BooleanExpression notSelf(Long loginUserId) {
        return user.userId.ne(loginUserId);
    }

    private BooleanExpression notProjectMember(Long projectId) {
        if (projectId == null) return null;
        return JPAExpressions.selectOne()
                .from(projectMember)
                .where(
                        projectMember.project.projectId.eq(projectId),
                        projectMember.user.userId.eq(user.userId)
                )
                .notExists();
    }
}
