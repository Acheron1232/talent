package com.mykyda.talantsocials.database.repository;

import com.mykyda.talantsocials.database.entity.Comment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
    List<Comment> findAllByPostIdAndIsAReplyNot(UUID postId, Boolean bool, PageRequest pageRequest);

    List<Comment> findAllByOriginalCommentId(UUID originalCommentId, PageRequest pageRequest);
}
