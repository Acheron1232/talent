package com.mykyda.talantsocials.dto.response;

import com.mykyda.talantsocials.database.entity.Profile;
import com.mykyda.talantsocials.database.enums.ProfileStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileDTO {

    private Long id;

    private String tag;

    private String displayName;

    private String profilePictureUrl;

    private String bannerPictureUrl;

    private ProfileStatus status;

    private String bioMarkdown;

    private Long followersAmount;

    private Long followingAmount;

//    private List<LanguageSkillDTO> languageSkills;
//
//    private List<JobSkillDTO> jobsSkills;

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
                .profilePictureUrl(profile.getProfilePictureUrl())
                .bannerPictureUrl(profile.getBannerPictureUrl())
                .status(profile.getStatus())
                .bioMarkdown(profile.getBioMarkdown())
                .followersAmount(profile.getFollowersAmount())
                .followingAmount(profile.getFollowingAmount())
//                .languageSkills(profile.getLanguageSkills().stream().map(LanguageSkillDTO::of).collect(Collectors.toList()))
//                .jobsSkills(profile.getJobsSkills().stream().map(JobSkillDTO::of).collect(Collectors.toList()))
                .build();
    }
}
