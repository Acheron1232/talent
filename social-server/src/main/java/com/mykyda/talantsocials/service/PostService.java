package com.mykyda.talantsocials.service;

import com.mykyda.talantsocials.database.entity.Post;
import com.mykyda.talantsocials.database.entity.Profile;
import com.mykyda.talantsocials.database.enums.UserContentType;
import com.mykyda.talantsocials.database.repository.PostRepository;
import com.mykyda.talantsocials.dto.create.PostCreationDTO;
import com.mykyda.talantsocials.dto.response.PostDTO;
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
public class PostService {

    private final PostRepository postRepository;

    private final EntityManager entityManager;

    private final ProfileService profileService;

    private final FollowService followService;

    @Transactional(readOnly = true)
    public List<PostDTO> findByProfileIdPaged(Long profileId, PageRequest pageRequest) {
        try {
            var posts = postRepository.findAllByProfileIdOrderByCreatedAtDesc(profileId, pageRequest)
                    .stream()
                    .map(PostDTO::of)
                    .toList();
            log.debug("posts found: {} for profile id {}", posts, profileId);
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
                log.debug("post found with id {}", post.getId());
                return PostDTO.of(post);
            } else {
                throw new EntityNotFoundException("post not found");
            }
        } catch (DataAccessException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Transactional
    public void create(Long userId, PostCreationDTO postDTO) {
        var profileId = profileService.getById(userId).getId();
        try {
            var reposted = postDTO.isReposted();
            if (reposted) {
                var checkOriginalPost = postRepository.findById(postDTO.getOriginalPostId());
                if (checkOriginalPost.isEmpty()) {
                    throw new EntityNotFoundException("original post not found with id " + postDTO.getOriginalPostId());
                }
                var postToSave = Post.builder()
                        .contentType(UserContentType.POST)
                        .reposted(postDTO.isReposted())
                        .originalPost(entityManager.getReference(Post.class, postDTO.getOriginalPostId()))
                        .profile(entityManager.getReference(Profile.class, profileId))
                        .textContent(postDTO.getTextContent())
                        .build();
                postRepository.save(postToSave);
            } else {
                var postToSave = Post.builder()
                        .contentType(UserContentType.POST)
                        .reposted(postDTO.isReposted())
                        .profile(entityManager.getReference(Profile.class, profileId))
                        .textContent(postDTO.getTextContent())
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
        var profileId = profileService.getById(userId).getId();
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

    //Todo: filter by last profile entrance date
    @Transactional(readOnly = true)
    public List<PostDTO> exploreFriends(Long userId, PageRequest pageRequest) {
        var follows = followService.getAllFollows(userId);
        var followsIds = follows.stream().map(f -> f.getFollowed().getId()).toList();
        return postRepository.findFollowsPosts(followsIds, pageRequest).stream().map(PostDTO::of).toList();
    }

    @Transactional(readOnly = true)
    public List<PostDTO> exploreGeneral(Long userId, PageRequest pageRequest) {
         return postRepository.findRandom(userId, pageRequest).stream().map(PostDTO::of).toList();
    }
}
