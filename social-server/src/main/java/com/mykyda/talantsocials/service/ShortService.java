package com.mykyda.talantsocials.service;

import com.mykyda.talantsocials.database.entity.Profile;
import com.mykyda.talantsocials.database.entity.Short;
import com.mykyda.talantsocials.database.entity.ShortElement;
import com.mykyda.talantsocials.database.entity.Tag;
import com.mykyda.talantsocials.database.enums.UserContentType;
import com.mykyda.talantsocials.database.mapper.ShortMapper;
import com.mykyda.talantsocials.database.repository.ProfileRepository;
import com.mykyda.talantsocials.database.repository.ShortRepository;
import com.mykyda.talantsocials.database.repository.TagRepository;
import com.mykyda.talantsocials.dto.create.ShortCreationDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShortService {
    private final ShortRepository shortRepository;
    private final TagRepository tagRepository;
    private final ProfileRepository profileRepository; //TODO
    private final ShortMapper shortMapper;

    @Transactional
    public void save(ShortCreationDto shortCreationDto, Long id) {
        Profile profile = profileRepository.findById(id).get();
        Short shorT = shortMapper.toEntity(shortCreationDto);
        shorT.setProfile(profile);
        List<ShortElement> elements = shortMapper.toShortElementList(shortCreationDto.elements());
        elements.forEach(e -> e.setShorT(shorT));
        shorT.setElements(elements);

        List<Tag> tags = shortMapper.mapTags(shortCreationDto.tags()).stream()
                .map(tag -> tagRepository.findByName(tag.getName())
                        .orElseGet(() -> tagRepository.save(tag)))
                .collect(Collectors.toList());
        shorT.setTags(tags);
        shorT.setContentType(UserContentType.SHORT);
        shortRepository.save(shorT);
    }

    public List<Short> findAll(Integer size, Long id) {
        return shortRepository.findRandom(Pageable.ofSize(size));
    }

    public List<Short> findAllExcluding(Integer size, Long id, List<UUID> exclude) {
        if (exclude == null || exclude.isEmpty()) {
            return findAll(size, id);
        }
        return shortRepository.findRandomExcluding(size, exclude);
    }
}
