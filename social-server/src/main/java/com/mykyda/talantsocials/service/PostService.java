package com.mykyda.talantsocials.service;

import com.mykyda.talantsocials.database.entity.Post;
import com.mykyda.talantsocials.database.entity.Profile;
import com.mykyda.talantsocials.database.repository.PostRepository;
import com.mykyda.talantsocials.dto.PostDTO;
import com.mykyda.talantsocials.dto.create.PostCreationDTO;
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

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    private final EntityManager entityManager;

    private final ProfileService profileService;

    @Transactional(readOnly = true)
    public List<PostDTO> findByProfileIdPaged(UUID profileId, PageRequest pageRequest) {
        try {
            var posts = postRepository.findAllByProfileIdOrderByCreatedAt(profileId, pageRequest).stream().map(PostDTO::of).toList();
            log.info("posts found: {} for profile id {}", posts, profileId);
            return posts;
        } catch (DataAccessException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public PostDTO findById(UUID postId) {
        try {
            var postOptional = postRepository.findById(postId);
            if (postOptional.isPresent()) {
                var post = postOptional.get();
                return PostDTO.of(post);
            } else {
                throw new EntityNotFoundException("post not found");
            }
        } catch (DataAccessException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Transactional
    public void post(Long userId, PostCreationDTO postDTO) {
        var profileId = profileService.checkByUserId(userId);
        try {
            var reposted = postDTO.isReposted();
            if (reposted) {
                var checkOriginalPost = postRepository.findById(postDTO.getOriginalPostId());
                if (checkOriginalPost.isEmpty()) {
                    throw new EntityNotFoundException("original post not found with id " + postDTO.getOriginalPostId());
                }
                var postToSave = Post.builder()
                        .reposted(postDTO.isReposted())
                        .originalPost(entityManager.getReference(Post.class, postDTO.getOriginalPostId()))
                        .profile(entityManager.getReference(Profile.class, profileId))
                        .textContent(postDTO.getTextContent())
                        .createdAt(Timestamp.from(Instant.now()))
                        .build();
                postRepository.save(postToSave);
            } else {
                var postToSave = Post.builder()
                        .reposted(postDTO.isReposted())
                        .profile(entityManager.getReference(Profile.class, profileId))
                        .textContent(postDTO.getTextContent())
                        .createdAt(Timestamp.from(Instant.now()))
                        .build();
                postRepository.save(postToSave);
            }
            log.info("post saved");
        } catch (DataAccessException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Transactional
    public void delete(Long userId, UUID postId) {
        var profileId = profileService.checkByUserId(userId);
        try {
            var checkById = postRepository.findById(postId);
            if (checkById.isEmpty()) {
                throw new EntityNotFoundException("post not found with id " + postId);
            }
            if (!checkById.get().getProfile().getId().equals(profileId)) {
                throw new ForbiddenAccessException("can`t delete post you do not own");
            }
            postRepository.delete(checkById.get());
            log.info("post deleted: {}", postId);
        } catch (DataAccessException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    public List<PostDTO> explore() {
        return null;
    }

    @Transactional
    public void like(UUID postId) {
        try {
            postRepository.incrementLikes(postId);
        } catch (DataAccessException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Transactional
    public void unlike(UUID postId) {
        try {
            postRepository.decrementLikes(postId);
        } catch (DataAccessException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Transactional
    public UUID checkById(UUID uuid) {
        try {
            var checkById = postRepository.findById(uuid);
            if (checkById.isEmpty()) {
                throw new EntityNotFoundException("Post with id " + uuid + " not found");
            }
            return checkById.get().getId();
        } catch (DataAccessException e) {
            throw new DatabaseException(e.getMessage());
        }
    }
}
