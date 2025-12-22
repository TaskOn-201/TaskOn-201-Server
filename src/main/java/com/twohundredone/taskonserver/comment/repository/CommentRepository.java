package com.twohundredone.taskonserver.comment.repository;

import com.twohundredone.taskonserver.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByTask_TaskIdOrderByCreatedAtAsc(Long taskId);
    void deleteAllByTask_TaskId(Long taskId);

    @Modifying
    @Query("update Comment c set c.user = null where c.user.userId = :userId")
    void clearUserReference(@Param("userId") Long userId);
}
