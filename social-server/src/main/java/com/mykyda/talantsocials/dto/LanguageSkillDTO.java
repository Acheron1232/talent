package com.mykyda.talantsocials.dto;

import com.mykyda.talantsocials.database.entity.LanguageSkill;
import com.mykyda.talantsocials.database.entity.Profile;
import com.mykyda.talantsocials.database.enums.LanguageProficiency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LanguageSkillDTO {

    private UUID id;

    private String languageCode;

    private LanguageProficiency languageProficiency;

    public static LanguageSkillDTO of(LanguageSkill languageSkill) {
        return LanguageSkillDTO.builder()
                .id(languageSkill.getId())
                .languageCode(languageSkill.getLanguageCode())
                .languageProficiency(languageSkill.getLanguageProficiency())
                .build();
    }

    public static LanguageSkill toEntity(LanguageSkillDTO languageSkillDTO, Profile profile) {
        return LanguageSkill.builder()
                .languageCode(languageSkillDTO.getLanguageCode())
                .languageProficiency(languageSkillDTO.getLanguageProficiency())
                .profile(profile)
                .build();
    }
}
