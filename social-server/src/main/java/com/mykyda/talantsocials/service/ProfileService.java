package com.mykyda.talantsocials.service;

import com.mykyda.talantsocials.database.entity.PostPreference;
import com.mykyda.talantsocials.database.entity.Profile;
import com.mykyda.talantsocials.database.repository.ProfileRepository;
import com.mykyda.talantsocials.dto.JobSkillDTO;
import com.mykyda.talantsocials.dto.LanguageSkillDTO;
import com.mykyda.talantsocials.dto.ProfileDTO;
import com.mykyda.talantsocials.dto.create.ProfileCreationDTO;
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


    @Transactional(readOnly = true)
    public ProfileDTO getCurrentProfile(Long id) {
        try {
            var profile = profileRepository.findById(id);
            if (profile.isPresent()) {
                log.info("profile found with id {}", id);
                return ProfileDTO.ofFull(profile.get());
            } else {
                throw new EntityNotFoundException("Profile with id " + id + " not found");
            }
        } catch (DataAccessException e) {
            throw new DatabaseException(e.getMessage());
        }
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
    public void createProfile(ProfileCreationDTO profileCreationDTO) {
        try {
            checkById(profileCreationDTO.id());
            checkByTag(profileCreationDTO.tag());
            var profileToSave = Profile.builder()
                    .id(profileCreationDTO.id())
                    .displayName(profileCreationDTO.displayName())
                    .profilePictureUrl(profileCreationDTO.profilePictureUrl())
                    .tag(profileCreationDTO.tag())
                    .build();

            PostPreference pref = PostPreference.builder()
                    .liked("")
                    .exclude("")
                    .profile(profileToSave)
                    .build();

            profileToSave.setPostPreference(pref);
            var profile = profileRepository.save(profileToSave);
            log.info("profile saved with id {}", profile.getId());
        } catch (DataAccessException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Transactional
    public void patchProfile(Long id, ProfileDTO patchedDto) {
        try {
            var checkById = getById(id);
            var profile = editProfile(patchedDto, checkById);
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
    public void patchTag(Long id, ProfileDTO patchedDto) {
        try {
            var profile = getById(id);
            checkByTag(patchedDto.getTag());
            profile.setTag(patchedDto.getTag());
            profileRepository.save(profile);
            log.info("tag {} set at profile with id {}", patchedDto.getTag(), profile.getId());
        } catch (DataAccessException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Transactional
    public void checkById(Long id) {
        try {
            var checkByUserId = profileRepository.findById(id);
            if (checkByUserId.isPresent()) {
                throw new EntityConflictException("Profile with id " + id + " already exists");
            }
        } catch (DataAccessException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Transactional
    public Profile getById(Long id) {
        try {
            var checkById = profileRepository.findById(id);
            if (checkById.isEmpty()) {
                throw new EntityNotFoundException("Profile with id " + id + " not found");
            }
            return checkById.get();
        } catch (DataAccessException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Transactional
    public void checkByTag(String tag) {
        try {
            var checkByTag = profileRepository.findByTag(tag);
            if (checkByTag.isPresent()) {
                throw new EntityConflictException("Profile with tag " + tag + " already exists");
            }
        } catch (DataAccessException e) {
            throw new DatabaseException(e.getMessage());
        }
    }
}
