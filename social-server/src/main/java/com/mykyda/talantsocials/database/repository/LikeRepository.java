package com.mykyda.talantsocials.database.repository;

import com.mykyda.talantsocials.database.entity.Like;
import com.mykyda.talantsocials.database.id.LikeId;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LikeRepository extends JpaRepository<Like, LikeId> {

    List<Like> findAllByPostId(UUID postId, PageRequest pageRequest);
}
