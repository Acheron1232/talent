package com.mykyda.talantsocials.dto.create;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.UUID;

public record PostCreationDTO(

        @JsonProperty("reposted")
        boolean reposted,

        List<PostElementCreationDto> elements,

        UUID originalPostId,

        String description,

        List<String> tags) {

    public record PostElementCreationDto(
            String type,
            String url,
            Integer orderIndex
    ) {
    }
}
