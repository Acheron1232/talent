package com.mykyda.talantsocials.dto.response;

import com.mykyda.talantsocials.database.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {

    private UUID id;

    private Boolean reposted;

    private ProfileDTO profile;

    private PostDTO originalPost;

    private String textContent;

    private Instant createdAt;

    private Long likesAmount;

    public static PostDTO of(Post post) {
        return PostDTO.builder()
                .id(post.getId())
                .reposted(Boolean.TRUE.equals(post.getReposted()))
                .profile(ProfileDTO.ofShort(post.getProfile()))
                .textContent(post.getTextContent())
                .originalPost(post.getReposted() ? PostDTO.original(post.getOriginalPost()) : null)
                .createdAt(post.getCreatedAt())
                .likesAmount(post.getLikesAmount())
                .build();
    }

    private static PostDTO original(Post post) {
        return PostDTO.builder()
                .id(post.getId())
                .profile(ProfileDTO.ofShort(post.getProfile()))
                .textContent(post.getTextContent())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
