package com.mykyda.talantsocials.dto;

import com.mykyda.talantsocials.database.entity.Follow;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowDTO {

    private ProfileDTO follower;

    private ProfileDTO followed;

    private Timestamp createdAt;

    public static FollowDTO of(Follow follow) {
        return FollowDTO.builder()
                .follower(ProfileDTO.ofShort(follow.getFollower()))
                .followed(ProfileDTO.ofShort(follow.getFollowed()))
                .createdAt(follow.getCreatedAt())
                .build();
    }
}
