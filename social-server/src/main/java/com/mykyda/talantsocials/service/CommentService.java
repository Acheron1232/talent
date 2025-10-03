package com.mykyda.talantsocials.service;

import com.mykyda.talantsocials.database.entity.Comment;
import com.mykyda.talantsocials.database.entity.ContentEntity;
import com.mykyda.talantsocials.database.entity.Profile;
import com.mykyda.talantsocials.database.repository.CommentRepository;
import com.mykyda.talantsocials.dto.CommentDTO;
import com.mykyda.talantsocials.dto.create.CommentCreationDTO;
import com.mykyda.talantsocials.exception.DatabaseException;
import com.mykyda.talantsocials.exception.EntityNotFoundException;
import com.mykyda.talantsocials.exception.ForbiddenAccessException;
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

    private final ProfileService profileService;

    @Transactional
    public void createComment(Long userId, CommentCreationDTO commentCreationDTO) {
        var profileId = profileService.getById(userId).getId();
        try {
            var isAReply = commentCreationDTO.isAReply();
            if (isAReply) {
                var checkOriginalComment = commentRepository.findById(commentCreationDTO.getOriginalCommentId());
                if (checkOriginalComment.isEmpty()) {
                    throw new EntityNotFoundException("Original comment not found with id " + commentCreationDTO.getOriginalCommentId());
                }
                var commentToSave = Comment.builder()
                        .contentEntity(entityManager.getReference(ContentEntity.class, commentCreationDTO.getContentEntityId()))
                        .profile(entityManager.getReference(Profile.class, profileId))
                        .isAReply(true)
                        .originalComment(entityManager.getReference(Comment.class, checkOriginalComment.get().getId()))
                        .content(commentCreationDTO.getContent())
                        .build();
                commentRepository.save(commentToSave);
            } else {
                var commentToSave = Comment.builder()
                        .contentEntity(entityManager.getReference(ContentEntity.class, commentCreationDTO.getContentEntityId()))
                        .profile(entityManager.getReference(Profile.class, profileId))
                        .content(commentCreationDTO.getContent())
                        .build();
                commentRepository.save(commentToSave);
            }
            log.info("post with id {} commented by profile id {}, with content {}",
                    commentCreationDTO.getContentEntityId(),
                    profileId,
                    commentCreationDTO.getContent());
        } catch (DataAccessException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    //TODO:check post

    @Transactional
    public void deleteComment(Long userId, UUID commentId) {
        var profileId = profileService.getById(userId).getId();
        try {
            var checkById = commentRepository.findById(commentId);
            if (checkById.isEmpty()) {
                throw new EntityNotFoundException("There is no comment with id " + commentId);
            }
            var comment = checkById.get();
            if (!profileId.equals(comment.getProfile().getId())) {
                throw new ForbiddenAccessException("Can't delete comment you do not own");
            }
            commentRepository.delete(comment);
            log.info("Comment with id {} for post by id {} have been deleted by profile {}",
                    commentId,
                    comment.getContentEntity().getId(),
                    profileId);
        } catch (DataAccessException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<CommentDTO> getCommentsForPostPaged(UUID postId, PageRequest pageRequest) {
        try {
            var comments = commentRepository.findAllByContentEntityIdAndIsAReplyNotOrderByCreatedAt(postId, true, pageRequest)
                    .stream()
                    .map(CommentDTO::of)
                    .toList();
            log.info("comments for post {} acquired", postId);
            return comments;
        } catch (DataAccessException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<CommentDTO> getRepliesPaged(UUID commentId, PageRequest pageRequest) {
        try {
            var comments = commentRepository.findAllByOriginalCommentIdOrderByCreatedAt(commentId, pageRequest)
                    .stream()
                    .map(CommentDTO::of)
                    .toList();
            log.info("replies for comment {} acquired", commentId);
            return comments;
        } catch (DataAccessException e) {
            throw new DatabaseException(e.getMessage());
        }
    }
}
