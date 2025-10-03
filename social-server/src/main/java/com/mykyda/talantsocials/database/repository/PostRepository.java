package com.mykyda.talantsocials.database.repository;

import com.mykyda.talantsocials.database.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {

    List<Post> findAllByProfileIdOrderByCreatedAt(Long profileId, Pageable pageRequest);

    @Modifying
    @Query("UPDATE Post p SET p.likesAmount = p.likesAmount + 1 WHERE p.id = :postId")
    void incrementLikes(@Param("postId") UUID postId);

    @Modifying
    @Query("UPDATE Post p SET p.likesAmount = p.likesAmount - 1 WHERE p.id = :postId")
    void decrementLikes(@Param("postId") UUID postId);
}
