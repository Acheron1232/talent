package com.mykyda.talantsocials.database.repository;

import com.mykyda.talantsocials.database.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TagRepository extends JpaRepository<Tag, UUID> {
}
