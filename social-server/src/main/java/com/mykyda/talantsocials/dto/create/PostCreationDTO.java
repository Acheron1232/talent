package com.mykyda.talantsocials.dto.create;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostCreationDTO{

        @JsonProperty("reposted")
        boolean reposted;

        UUID profileId;

        UUID originalPostId;

        String textContent;
}
