package com.mykyda.talantsocials.service;

import com.mykyda.talantsocials.dto.create.ProfileCreationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreationImitatorService {

    private final ProfileService profileService;


    public String createProfile(Long userId, ProfileCreationDTO profileCreationDTO) {
        return profileService.createProfile(userId, profileCreationDTO);
    }
}
