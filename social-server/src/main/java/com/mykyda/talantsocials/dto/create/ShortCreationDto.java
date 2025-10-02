package com.mykyda.talantsocials.dto.create;


import java.util.List;

public record ShortCreationDto(
        String type,
        List<ShortElementCreationDto> elements,
        List<String> tags,
        String description,
        Boolean isPublic
) {
    public record ShortElementCreationDto(
            String type,
            String url,
            Integer orderIndex
    ){}
}
