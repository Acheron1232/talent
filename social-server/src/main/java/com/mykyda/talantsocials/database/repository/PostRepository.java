package com.mykyda.talantsocials.database.repository;

import com.mykyda.talantsocials.database.entity.Post;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {

    List<Post> findAllByProfileId(UUID profileId, PageRequest pageRequest);
}
