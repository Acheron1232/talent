package com.mykyda.talantsocials.service;

import com.mykyda.talantsocials.database.entity.PostPreference;
import com.mykyda.talantsocials.database.entity.Profile;
import com.mykyda.talantsocials.database.repository.ProfileRepository;
import com.mykyda.talantsocials.dto.JobSkillDTO;
import com.mykyda.talantsocials.dto.LanguageSkillDTO;
import com.mykyda.talantsocials.dto.create.ProfileCreationDTO;
import com.mykyda.talantsocials.dto.ProfileDTO;
import com.mykyda.talantsocials.exception.DatabaseException;
import com.mykyda.talantsocials.exception.EntityConflictException;
import com.mykyda.talantsocials.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;

    //Todo:rework
    public ProfileDTO getCurrentProfile() {
        return null;
    }

    @Transactional(readOnly = true)
    public ProfileDTO getProfileByTag(String profileTag) {
        profileTag = profileTag.replaceAll("@", "");
        try {
            var profile = profileRepository.findByTag(profileTag);
            if (profile.isPresent()) {
                log.info("profile found with tag {}", profileTag);
                return ProfileDTO.ofFull(profile.get());
            } else {
                throw new EntityNotFoundException("Profile with tag " + profileTag + " not found");
            }
        } catch (DataAccessException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Transactional
    public String createProfile(Long userId, ProfileCreationDTO profileCreationDTO) {
        try {
            var checkById = profileRepository.findByUserId(userId);
            if (checkById.isPresent()) {
                throw new EntityConflictException("Profile for user id " + userId + " already exists");
            }
            var checkByTag = profileRepository.findByTag(profileCreationDTO.tag());
            if (checkByTag.isPresent()) {
                throw new EntityConflictException("Profile with tag " + profileCreationDTO.tag() + " already exists");
            }
            var profileToSave = Profile.builder()
                    .userId(userId)
                    .displayName(profileCreationDTO.displayName())
                    .tag(profileCreationDTO.tag())
                    .build();

            PostPreference pref = PostPreference.builder()
                    .liked("")
                    .exclude("")
                    .profile(profileToSave)
                    .build();

            profileToSave.setPostPreference(pref);
            var profile = profileRepository.save(profileToSave);
            log.info("profile saved with id {} for user id {}", profile.getId(), profile.getUserId());
            return profile.getTag();
        } catch (DataAccessException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Transactional
    public void patchProfileById(ProfileDTO patchedDto) {
        try {
            var checkById = profileRepository.findById(patchedDto.getId());
            if (checkById.isEmpty()) {
                throw new EntityNotFoundException("Profile with id " + patchedDto.getId() + " not found");
            }
            var profile = editProfile(patchedDto, checkById.get());
            profileRepository.save(profile);
            log.info("profile updated with id {}", profile.getId());
        } catch (DataAccessException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    private static Profile editProfile(ProfileDTO patchedDto, Profile profile) {
        if (patchedDto.getDisplayName() != null && !patchedDto.getDisplayName().isBlank()) {
            profile.setDisplayName(patchedDto.getDisplayName());
        }
        if (patchedDto.getCurrentOccupation() != null && !patchedDto.getCurrentOccupation().isBlank()) {
            profile.setCurrentOccupation(patchedDto.getCurrentOccupation());
        }
        if (patchedDto.getProfilePictureUrl() != null) {
            profile.setProfilePictureUrl(patchedDto.getProfilePictureUrl());
        }
        if (patchedDto.getBannerPictureUrl() != null) {
            profile.setBannerPictureUrl(patchedDto.getBannerPictureUrl());
        }
        if (patchedDto.getStatus() != null) {
            profile.setStatus(patchedDto.getStatus());
        }
        if (patchedDto.getBioMarkdown() != null && !patchedDto.getBioMarkdown().isBlank()) {
            profile.setBioMarkdown(patchedDto.getBioMarkdown());
        }

        if (patchedDto.getLanguageSkills() != null) {
            profile.getLanguageSkills().clear();
            profile.getLanguageSkills().addAll(
                    patchedDto.getLanguageSkills().stream()
                            .map(e -> LanguageSkillDTO.toEntity(e, profile))
                            .toList());
        }

        if (patchedDto.getJobsSkills() != null) {
            profile.getJobsSkills().clear();
            profile.getJobsSkills().addAll(
                    patchedDto.getJobsSkills().stream()
                            .map(e -> JobSkillDTO.toEntity(e, profile))
                            .toList());
        }
        return profile;
    }

    @Transactional
    public void patchTagById(ProfileDTO patchedDto) {
        try {
            var profileOptional = profileRepository.findById(patchedDto.getId());
            if (profileOptional.isEmpty()) {
                throw new EntityNotFoundException("Profile with id " + patchedDto.getId() + " not found");
            }
            var checkByTag = profileRepository.findByTag(patchedDto.getTag());
            if (checkByTag.isPresent()) {
                throw new EntityConflictException("Profile with tag" + patchedDto.getTag() + " already exists");
            }
            var profile = profileOptional.get();
            profile.setTag(patchedDto.getTag());
            profileRepository.save(profile);
            log.info("tag {} set at profile with id {}", patchedDto.getTag(), profile.getId());
        } catch (DataAccessException e) {
            throw new DatabaseException(e.getMessage());
        }
    }
}
