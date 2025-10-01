package com.mykyda.talantsocials.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ShortRepository extends JpaRepository<Short, UUID> {
}
