package com.mykyda.talantsocials.service;

import com.mykyda.talantsocials.database.entity.PostPreference;
import com.mykyda.talantsocials.database.entity.Profile;
import com.mykyda.talantsocials.database.repository.ProfileRepository;
import com.mykyda.talantsocials.dto.create.ProfileCreationDTO;
import com.mykyda.talantsocials.dto.patch.ProfilePatchDTO;
import com.mykyda.talantsocials.dto.patch.ProfilePatchTagDTO;
import com.mykyda.talantsocials.dto.response.ProfileDTO;
import com.mykyda.talantsocials.exception.DatabaseException;
import com.mykyda.talantsocials.exception.EntityConflictException;
import com.mykyda.talantsocials.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;

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
                log.debug("profile found with id {}", id);
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
                log.debug("profile found with tag {}", profileTag);
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
    public void patchProfile(Long id, ProfilePatchDTO patchedDto) {
        try {
            var checkById = getById(id);
            var profile = editProfile(patchedDto, checkById);
            profileRepository.save(profile);
            log.info("profile updated with id {}", profile.getId());
        } catch (DataAccessException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    private static Profile editProfile(ProfilePatchDTO patchedDto, Profile profile) {
        updateIfNotNull(patchedDto.displayName(), profile::setDisplayName);
        updateIfNotNull(patchedDto.bioMarkdown(), profile::setBioMarkdown);
        return profile;
    }

    private static <T> void updateIfNotNull(T value, Consumer<T> setter) {
        if (value != null) {
            setter.accept(value);
        }
    }

    @Transactional
    public void patchTag(Long id, ProfilePatchTagDTO patchedDto) {
        try {
            var profile = getById(id);
            checkByTag(patchedDto.tag());
            profile.setTag(patchedDto.tag());
            profileRepository.save(profile);
            log.info("tag {} set at profile with id {}", patchedDto.tag(), profile.getId());
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

    @Transactional
    public void incFollowers(Long followedId) {
        try {
            profileRepository.incrementFollowers(followedId);
        } catch (DataAccessException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Transactional
    public void decFollowers(Long followedId) {
        try {
            profileRepository.decrementFollowers(followedId);
        } catch (DataAccessException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Transactional
    public void incFollowed(Long followerId) {
        try {
            profileRepository.incrementFollowed(followerId);
        } catch (DataAccessException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Transactional
    public void decFollowed(Long followerId) {
        try {
            profileRepository.decrementFollowed(followerId);
        } catch (DataAccessException e) {
            throw new DatabaseException(e.getMessage());
        }
    }
}
