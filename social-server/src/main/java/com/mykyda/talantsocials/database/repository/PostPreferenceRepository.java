package com.mykyda.talantsocials.database.repository;

import com.mykyda.talantsocials.database.entity.PostPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PostPreferenceRepository extends JpaRepository<PostPreference, UUID> {
}
