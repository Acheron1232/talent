package com.mykyda.talantsocials.database.repository;

import com.mykyda.talantsocials.database.entity.ShortElement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ShortElementRepository extends JpaRepository<ShortElement, UUID> {
}
