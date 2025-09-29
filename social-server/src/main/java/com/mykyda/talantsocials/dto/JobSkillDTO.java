package com.mykyda.talantsocials.dto;

import com.mykyda.talantsocials.database.entity.JobSkill;
import com.mykyda.talantsocials.database.entity.Profile;
import com.mykyda.talantsocials.database.enums.JobExperience;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobSkillDTO {

    private UUID id;

    private String jobTitle;

    private JobExperience experience;


    public static JobSkillDTO of(JobSkill jobSkill) {
        return JobSkillDTO.builder()
                .id(jobSkill.getId())
                .jobTitle(jobSkill.getJobTitle())
                .experience(jobSkill.getExperience())
                .build();
    }

    public static JobSkill toEntity(JobSkillDTO jobSkillDTO, Profile profile) {
        return JobSkill.builder()
                .jobTitle(jobSkillDTO.getJobTitle())
                .experience(jobSkillDTO.getExperience())
                .profile(profile)
                .build();
    }
}
