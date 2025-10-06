package com.mykyda.talantsocials.dto;

import com.mykyda.talantsocials.database.entity.Like;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeDTO {

    private ProfileDTO profile;

    private Instant createdAt;

    public static LikeDTO of(Like like) {
        return LikeDTO.builder()
                .profile(ProfileDTO.ofShort(like.getProfile()))
                .createdAt(like.getCreatedAt())
                .build();
    }
}
