package com.mykyda.talantsocials.dto;

import com.mykyda.talantsocials.database.entity.Profile;
import com.mykyda.talantsocials.database.enums.ProfileStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileDTO {

    private UUID id;

    private String tag;

    private String displayName;

    private String currentOccupation;

    private String profilePictureUrl;

    private String bannerPictureUrl;

    private ProfileStatus status;

    private Double employeeRating;

    private String bioMarkdown;

    private List<LanguageSkillDTO> languageSkills;

    private List<JobSkillDTO> jobsSkills;

    public static ProfileDTO ofShort(Profile profile) {
        return ProfileDTO.builder()
                .tag(profile.getTag())
                .displayName(profile.getDisplayName())
                .profilePictureUrl(profile.getProfilePictureUrl())
                .build();
    }

    public static ProfileDTO ofFull(Profile profile) {
        return ProfileDTO.builder()
                .id(profile.getId())
                .tag(profile.getTag())
                .displayName(profile.getDisplayName())
                .currentOccupation(profile.getCurrentOccupation())
                .profilePictureUrl(profile.getProfilePictureUrl())
                .bannerPictureUrl(profile.getBannerPictureUrl())
                .status(profile.getStatus())
                .employeeRating(profile.getEmployeeRating())
                .bioMarkdown(profile.getBioMarkdown())
                .languageSkills(profile.getLanguageSkills().stream().map(LanguageSkillDTO::of).collect(Collectors.toList()))
                .jobsSkills(profile.getJobsSkills().stream().map(JobSkillDTO::of).collect(Collectors.toList()))
                .build();
    }
}
