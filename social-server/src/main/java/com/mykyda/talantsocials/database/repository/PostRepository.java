package com.mykyda.talantsocials.database.repository;

import com.mykyda.talantsocials.database.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {

    List<Post> findAllByProfileIdOrderByCreatedAtDesc(Long profileId, Pageable pageRequest);

    @Query("select p from Post p  WHERE p.profile.id IN :followsIds ORDER BY p.createdAt DESC ")
    List<Post> findFollowsPosts(@Param("followsIds") List<Long> followsIds,
                                Pageable pageRequest);

    @Query("SELECT p FROM Post p WHERE NOT p.profile.id =:profileId ORDER BY function('RANDOM')")
    List<Post> findRandom(@Param("profileId") Long profileId, Pageable pageable);
}
