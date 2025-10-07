package com.mykyda.talantsocials.dto.patch;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProfilePatchTagDTO(

        @NotNull(message = "tag shouldn`t be null")
        @NotBlank
        String tag) {
}
