package com.mykyda.talantsocials.database.repository;

import com.mykyda.talantsocials.database.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, UUID> {

    Optional<Profile> findByUserId(Long userId);

    Optional<Profile> findByTag(String profileTag);
}
