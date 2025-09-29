package com.mykyda.talantsocials.service;

import com.mykyda.talantsocials.database.entity.Comment;
import com.mykyda.talantsocials.database.entity.Post;
import com.mykyda.talantsocials.database.entity.Profile;
import com.mykyda.talantsocials.database.repository.CommentRepository;
import com.mykyda.talantsocials.dto.CommentDTO;
import com.mykyda.talantsocials.dto.create.CommentCreationDTO;
import com.mykyda.talantsocials.exception.DatabaseException;
import com.mykyda.talantsocials.exception.EntityNotFoundException;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    private final EntityManager entityManager;

    @Transactional
    public void createComment(CommentCreationDTO commentCreationDTO) {
        try {
            var isAReply = commentCreationDTO.isAReply();
            if (isAReply) {
                var checkOriginalComment = commentRepository.findById(commentCreationDTO.getOriginalCommentId());
                if (checkOriginalComment.isEmpty()) {
                    throw new EntityNotFoundException("Original comment not found with id " + commentCreationDTO.getOriginalCommentId());
                }
                var commentToSave = Comment.builder()
                        .post(entityManager.getReference(Post.class, commentCreationDTO.getPostId()))
                        .profile(entityManager.getReference(Profile.class, commentCreationDTO.getProfileId()))
                        .isAReply(true)
                        .originalComment(checkOriginalComment.get())
                        .content(commentCreationDTO.getContent())
                        .build();
                commentRepository.save(commentToSave);

            } else {
                var commentToSave = Comment.builder()
                        .post(entityManager.getReference(Post.class, commentCreationDTO.getPostId()))
                        .profile(entityManager.getReference(Profile.class, commentCreationDTO.getProfileId()))
                        .content(commentCreationDTO.getContent())
                        .build();
                commentRepository.save(commentToSave);
            }
            log.info("post with id {} commented by profile id {}, with content {}", commentCreationDTO.getPostId(), commentCreationDTO.getProfileId(), commentCreationDTO.getContent());
        } catch (DataAccessException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Transactional
    public void deleteComment(CommentCreationDTO commentCreationDTO) {
        try {
            var checkById = commentRepository.findById(commentCreationDTO.getId());
            if (checkById.isEmpty()) {
                throw new EntityNotFoundException("There is no comment with id " + commentCreationDTO.getId());
            }
            var comment = checkById.get();
            commentRepository.delete(comment);
            log.info("Comment with id {} for post by id {} have been deleted by profile {}", commentCreationDTO.getId(), comment.getPost().getId(), comment.getProfile().getId());
        } catch (DataAccessException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<CommentDTO> getCommentsForPostPaged(UUID postId, PageRequest pageRequest) {
        try {
            var comments = commentRepository.findAllByPostIdAndIsAReplyNot(postId, true, pageRequest).stream().map(CommentDTO::of).toList();
            log.info("comments for post {} acquired", postId);
            return comments;
        } catch (DataAccessException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<CommentDTO> getRepliesPaged(UUID commentId, PageRequest pageRequest) {
        try {
            var comments = commentRepository.findAllByOriginalCommentId(commentId, pageRequest).stream().map(CommentDTO::of).toList();
            log.info("replies for comment {} acquired", commentId);
            return comments;
        } catch (DataAccessException e) {
            throw new DatabaseException(e.getMessage());
        }
    }
}
