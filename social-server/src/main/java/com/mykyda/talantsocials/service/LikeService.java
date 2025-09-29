package com.mykyda.talantsocials.service;

import com.mykyda.talantsocials.database.entity.Like;
import com.mykyda.talantsocials.database.entity.Post;
import com.mykyda.talantsocials.database.entity.Profile;
import com.mykyda.talantsocials.database.id.LikeId;
import com.mykyda.talantsocials.database.repository.LikeRepository;
import com.mykyda.talantsocials.dto.create.LikeCreationDTO;
import com.mykyda.talantsocials.dto.LikeDTO;
import com.mykyda.talantsocials.exception.DatabaseException;
import com.mykyda.talantsocials.exception.EntityConflictException;
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
public class LikeService {

    private final LikeRepository likeRepository;

    private final EntityManager entityManager;

    @Transactional
    public void createLike(LikeCreationDTO likeDTO) {
        try {
            var id = new LikeId(likeDTO.postId(), likeDTO.profileId());
            var checkById = likeRepository.findById(id);
            if (checkById.isPresent()) {
                throw new EntityConflictException("Post " + likeDTO.postId() + "already liked by " + likeDTO.profileId());
            }
            var likeToSave = Like.builder()
                    .id(id)
                    .post(entityManager.getReference(Post.class, likeDTO.postId()))
                    .profile(entityManager.getReference(Profile.class, likeDTO.profileId()))
                    .build();
            likeRepository.save(likeToSave);
            log.info("post with id {} liked by profile id {}", likeDTO.postId(), likeDTO.profileId());
        } catch (DataAccessException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Transactional
    public void deleteLike(LikeCreationDTO likeDTO) {
        try {
            var checkById = likeRepository.findById(new LikeId(likeDTO.postId(), likeDTO.profileId()));
            if (checkById.isEmpty()) {
                throw new EntityNotFoundException("Post " + likeDTO.postId() + "is not liked by " + likeDTO.profileId());
            }
            likeRepository.delete(checkById.get());
            log.info("like for post by id {} has been unliked by profile {}", likeDTO.postId(), likeDTO.profileId());
        } catch (DataAccessException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<LikeDTO> getLikesForPostPaged(UUID postId, PageRequest pageRequest) {
        try {
            var likes = likeRepository.findAllByPostId(postId, pageRequest).stream().map(LikeDTO::of).toList();
            log.info("likes for post {} acquired", postId);
            return likes;
        } catch (DataAccessException e) {
            throw new DatabaseException(e.getMessage());
        }
    }
}
