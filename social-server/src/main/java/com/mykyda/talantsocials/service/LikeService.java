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

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;

    private final EntityManager entityManager;

    private final ContentEntityService contentEntityService;

    private final ProfileService profileService;

    @Transactional
    public void createLike(Long userId, LikeCreationDTO likeDTO) {
        var profileId = profileService.getById(userId).getId();
        var contentId = contentEntityService.getById(likeDTO.contentEntityId());
        try {
            var id = new LikeId(contentId, profileId);
            var checkById = likeRepository.findById(id);
            if (checkById.isPresent()) {
                throw new EntityConflictException("Content " + contentId + "already liked by " + profileId);
            }
            var likeToSave = Like.builder()
                    .id(id)
                    .contentEntity(entityManager.getReference(ContentEntity.class, contentId))
                    .profile(entityManager.getReference(Profile.class, profileId))
                    .createdAt(Timestamp.from(Instant.now()))
                    .build();
            likeRepository.save(likeToSave);
            log.info("content with id {} liked by profile id {}", contentId, profileId);
            contentEntityService.like(contentId);
        } catch (DataAccessException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Transactional
    public void deleteLike(Long userId, LikeCreationDTO likeDTO) {
        var contentId = contentEntityService.getById(likeDTO.contentEntityId());
        var profileId = profileService.getById(userId).getId();
        try {
            var checkById = likeRepository.findById(new LikeId(contentId, profileId));
            if (checkById.isEmpty()) {
                throw new EntityNotFoundException("content " + contentId + "is not liked by " + profileId);
            }
            likeRepository.delete(checkById.get());
            log.info("like for content by id {} has been unliked by profile {}", contentId, profileId);
            contentEntityService.unlike(contentId);
        } catch (DataAccessException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<LikeDTO> getLikesPaged(UUID postId, PageRequest pageRequest) {
        try {
            var likes = likeRepository.findAllByContentEntityId(postId, pageRequest).stream().map(LikeDTO::of).toList();
            log.info("likes for content {} acquired", postId);
            return likes;
        } catch (DataAccessException e) {
            throw new DatabaseException(e.getMessage());
        }
    }
}
