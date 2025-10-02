package com.mykyda.talantsocials.database.mapper;

import com.mykyda.talantsocials.database.entity.Short;
import com.mykyda.talantsocials.database.entity.ShortElement;
import com.mykyda.talantsocials.database.entity.Tag;
import com.mykyda.talantsocials.dto.create.ShortCreationDto;
import org.mapstruct.*;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL
)
public interface ShortMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "profile", ignore = true)
    @Mapping(target = "likes", constant = "0L")
    @Mapping(target = "views", constant = "0L")
    @Mapping(target = "createdAt", expression = "java(java.time.Instant.now())")
    Short toEntity(ShortCreationDto dto);

    default Short.Type mapShortType(String type) {
        if (type == null) return null;
        return Short.Type.valueOf(type.toUpperCase());
    }

    default ShortElement.Type mapShortElementType(String type) {
        if (type == null) return null;
        return ShortElement.Type.valueOf(type.toUpperCase());
    }

    List<ShortElement> toShortElementList(List<ShortCreationDto.ShortElementCreationDto> elements);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "shorT", ignore = true) // back-reference, set in service
    @Mapping(target = "createdAt", expression = "java(java.time.Instant.now())")
    ShortElement toShortElement(ShortCreationDto.ShortElementCreationDto dto);

    default List<Tag> mapTags(List<String> tags) {
        if (tags == null) return null;
        return tags.stream()
                .map(name -> {
                    Tag tag = new Tag();
                    tag.setName(name);
                    return tag;
                })
                .collect(Collectors.toList());
    }
}
