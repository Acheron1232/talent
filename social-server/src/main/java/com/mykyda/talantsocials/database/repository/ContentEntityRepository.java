package com.mykyda.talantsocials.database.repository;

import com.mykyda.talantsocials.database.entity.ContentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ContentEntityRepository extends JpaRepository<ContentEntity, UUID> {

    @Modifying
    @Query("UPDATE ContentEntity c SET c.likesAmount = c.likesAmount + 1 WHERE c.id = :contentId")
    void incrementLikes(@Param("contentId") UUID contentId);

    @Modifying
    @Query("UPDATE ContentEntity c SET c.likesAmount = c.likesAmount - 1 WHERE c.id = :contentId")
    void decrementLikes(@Param("contentId") UUID contentId);
}
