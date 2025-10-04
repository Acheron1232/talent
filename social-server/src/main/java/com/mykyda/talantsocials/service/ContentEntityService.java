package com.mykyda.talantsocials.service;

import com.mykyda.talantsocials.database.repository.ContentEntityRepository;
import com.mykyda.talantsocials.exception.DatabaseException;
import com.mykyda.talantsocials.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContentEntityService {

    private final ContentEntityRepository contentEntityRepository;

    @Transactional(readOnly = true)
    public UUID getById(UUID id) {
        try {
            var checkById = contentEntityRepository.findById(id);
            if (checkById.isEmpty()) {
                throw new EntityNotFoundException("Content with id " + id + " not found");
            }
            return checkById.get().getId();
        } catch (DataAccessException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Transactional
    public void like(UUID postId) {
        try {
            contentEntityRepository.incrementLikes(postId);
        } catch (DataAccessException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Transactional
    public void unlike(UUID postId) {
        try {
            contentEntityRepository.decrementLikes(postId);
        } catch (DataAccessException e) {
            throw new DatabaseException(e.getMessage());
        }
    }
}
