package com.mykyda.talantsocials.dto.patch;

public record ProfilePatchDTO(

        String displayName,


        String bioMarkdown)

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
