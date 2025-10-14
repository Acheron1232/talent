package com.mykyda.talantsocials.dto.patch;

//import com.mykyda.talantsocials.dto.response.JobSkillDTO;
//import com.mykyda.talantsocials.dto.response.LanguageSkillDTO;

public record ProfilePatchDTO(

        String displayName,


        String bioMarkdown)

//        List<LanguageSkillDTO> languageSkills,

//        List<JobSkillDTO> jobsSkills)
{

    public ProfilePatchDTO {
        if (displayName != null) {
            if (displayName.isBlank()) {
                displayName = null;
            }
        }
        if (bioMarkdown != null) {
            if (bioMarkdown.isBlank()) {
                bioMarkdown = null;
            }
        }
    }
}
