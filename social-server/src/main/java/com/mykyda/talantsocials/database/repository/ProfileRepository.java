package com.mykyda.talantsocials.database.repository;

import com.mykyda.talantsocials.database.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByTag(String profileTag);

    @Modifying
    @Query("UPDATE Profile p SET p.followersAmount = p.followersAmount + 1 WHERE p.id = :profileId")
    void incrementFollowers(@Param("profileId") Long profileId);

    @Modifying
    @Query("UPDATE Profile p SET p.followersAmount = p.followersAmount - 1 WHERE p.id = :profileId")
    void decrementFollowers(@Param("profileId") Long profileId);

    @Modifying
    @Query("UPDATE Profile p SET p.followingAmount = p.followingAmount + 1 WHERE p.id = :profileId")
    void incrementFollowed(@Param("profileId") Long profileId);

    @Modifying
    @Query("UPDATE Profile p SET p.followingAmount = p.followingAmount - 1 WHERE p.id = :profileId")
    void decrementFollowed(@Param("profileId") Long profileId);
}
