package com.mykyda.talantsocials.dto.create;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record ProfileCreationDTO(
        @NotNull(message = "id is required")
        Long id,

        @JsonProperty(defaultValue = "John Doe")
        String displayName,

        @NotNull(message = "tag is required")
        String tag,

        String profilePictureUrl) {
    public ProfileCreationDTO {
        if (tag != null) {
            tag = tag.replaceAll(" ", "_");
        }
    }
}
