package com.twohundredone.taskonserver.comment.repository;

import com.twohundredone.taskonserver.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}
