package com.mykyda.talantsocials.database.repository;

import com.mykyda.talantsocials.database.entity.Follow;
import com.mykyda.talantsocials.database.id.FollowId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowRepository extends JpaRepository<Follow, FollowId> {

    List<Follow> findAllByFollowerId(Long followerId, Pageable pageable);

    List<Follow> findAllByFollowerId(Long followerId);

    List<Follow> findAllByFollowedId(Long followedId, Pageable pageable);
}
