package com.mykyda.talantsocials.service;

import com.mykyda.talantsocials.database.entity.ContentEntity;
import com.mykyda.talantsocials.database.entity.Like;
import com.mykyda.talantsocials.database.entity.Profile;
import com.mykyda.talantsocials.database.id.LikeId;
import com.mykyda.talantsocials.database.repository.LikeRepository;
import com.mykyda.talantsocials.dto.LikeDTO;
import com.mykyda.talantsocials.dto.create.LikeCreationDTO;
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

    private final PostService postService;

    private final ProfileService profileService;

    @Transactional
    public void createLike(Long userId, LikeCreationDTO likeDTO) {
        var profileId = profileService.getById(userId).getId();
        var postId = postService.checkById(likeDTO.contentEntityId());
        try {
            var id = new LikeId(postId, profileId);
            var checkById = likeRepository.findById(id);
            if (checkById.isPresent()) {
                throw new EntityConflictException("Post " + postId + "already liked by " + profileId);
            }
            var likeToSave = Like.builder()
                    .id(id)
                    .contentEntity(entityManager.getReference(ContentEntity.class, postId))
                    .profile(entityManager.getReference(Profile.class, profileId))
                    .build();
            likeRepository.save(likeToSave);
            log.info("post with id {} liked by profile id {}", postId, profileId);
            postService.like(postId);
        } catch (DataAccessException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Transactional
    public void deleteLike(Long userId, LikeCreationDTO likeDTO) {
        var postId = postService.checkById(likeDTO.contentEntityId());
        var profileId = profileService.getById(userId).getId();
        try {
            var checkById = likeRepository.findById(new LikeId(postId, profileId));
            if (checkById.isEmpty()) {
                throw new EntityNotFoundException("Post " + postId + "is not liked by " + profileId);
            }
            likeRepository.delete(checkById.get());
            log.info("like for post by id {} has been unliked by profile {}", postId, profileId);
            postService.unlike(postId);
        } catch (DataAccessException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<LikeDTO> getLikesForPostPaged(UUID postId, PageRequest pageRequest) {
        try {
            var likes = likeRepository.findAllByContentEntityId(postId, pageRequest).stream().map(LikeDTO::of).toList();
            log.info("likes for post {} acquired", postId);
            return likes;
        } catch (DataAccessException e) {
            throw new DatabaseException(e.getMessage());
        }
    }
}
