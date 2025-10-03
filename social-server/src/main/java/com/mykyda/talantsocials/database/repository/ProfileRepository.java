package com.mykyda.talantsocials.database.repository;

import com.mykyda.talantsocials.database.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByTag(String profileTag);
}
