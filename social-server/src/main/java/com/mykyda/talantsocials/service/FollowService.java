package com.mykyda.talantsocials.service;

import com.mykyda.talantsocials.database.entity.Follow;
import com.mykyda.talantsocials.database.entity.Profile;
import com.mykyda.talantsocials.database.id.FollowId;
import com.mykyda.talantsocials.database.repository.FollowRepository;
import com.mykyda.talantsocials.dto.FollowDTO;
import com.mykyda.talantsocials.exception.DatabaseException;
import com.mykyda.talantsocials.exception.EntityConflictException;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;

    private final EntityManager entityManager;

    private final ProfileService profileService;

    @Transactional(rollbackFor = DataAccessException.class)
    public void follow(Long followerId, Long followedId) {
        if (followerId.equals(followedId)) {
            throw new EntityConflictException("profile " + followerId + " cannot follow itself");
        }
        var follower = profileService.getById(followerId);
        var followed = profileService.getById(followedId);
        try {
            var id = new FollowId(follower.getId(), followed.getId());
            var checkById = followRepository.findById(id);
            if (checkById.isPresent()) {
                throw new EntityConflictException("profile " + followerId + " have already followed " + followedId);
            }
            var followToSave = Follow.builder()
                    .id(id)
                    .follower(entityManager.getReference(Profile.class, follower.getId()))
                    .followed(entityManager.getReference(Profile.class, followed.getId()))
                    .createdAt(Timestamp.from(Instant.now()))
                    .build();
            followRepository.save(followToSave);
            log.info("followed {} by {}", followedId, followerId);
            profileService.incFollowers(followedId);
            profileService.incFollowed(followerId);
        } catch (DataAccessException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Transactional
    public void unfollow(Long followerId, Long followedId) {
        var follower = profileService.getById(followerId);
        var followed = profileService.getById(followedId);
        try {
            var checkById = followRepository.findById(new FollowId(follower.getId(), followed.getId()));
            if (checkById.isEmpty()) {
                throw new EntityConflictException("profile " + followerId + " isn`t following " + followedId);
            }
            followRepository.delete(checkById.get());
            log.info("unfollowed {} by {}", followedId, followerId);
            profileService.decFollowers(followedId);
            profileService.decFollowed(followerId);
        } catch (DataAccessException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<FollowDTO> getFollows(Long followerId, PageRequest pageRequest) {
        try {
            var follows = followRepository.findAllByFollowerId(followerId, pageRequest).stream().map(FollowDTO::of).toList();
            log.info("follows for profile {} acquired", followerId);
            return follows;
        } catch (DataAccessException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<FollowDTO> getFollowedBy(Long followedId, PageRequest pageRequest) {
        try {
            var followedBy = followRepository.findAllByFollowedId(followedId, pageRequest).stream().map(FollowDTO::of).toList();
            log.info("followed by for profile {} acquired", followedId);
            return followedBy;
        } catch (DataAccessException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public boolean isFollowed(Long followerId, Long followedId) {
        try {
            var followedBy = followRepository.findById(new FollowId(followerId, followedId));
            if (followedBy.isPresent()) {
                log.debug(" {} followed by {}", followedId, followerId);
                return true;
            } else {
                log.debug(" {} not following {}", followedId, followerId);
                return false;
            }
        } catch (DataAccessException e) {
            throw new DatabaseException(e.getMessage());
        }
    }
}
