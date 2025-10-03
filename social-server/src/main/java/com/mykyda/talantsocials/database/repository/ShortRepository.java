package com.mykyda.talantsocials.database.repository;

import com.mykyda.talantsocials.database.entity.Short;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ShortRepository extends JpaRepository<Short, UUID> {

    @Query(value = "SELECT * FROM \"short\" ORDER BY RANDOM() LIMIT :size", nativeQuery = true)
    List<Short> findRandom(@Param("size") int size);

    @Query(value = "SELECT * FROM \"short\" WHERE id NOT IN (:exclude) ORDER BY RANDOM() LIMIT :size", nativeQuery = true)
    List<Short> findRandomExcluding(@Param("size") int size, @Param("exclude") List<UUID> exclude);
}
