package com.mykyda.talantsocials.dto;

import com.mykyda.talantsocials.database.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
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

    private Timestamp createdAt;

    public static PostDTO of(Post post) {
        return PostDTO.builder()
                .id(post.getId())
                .reposted(Boolean.TRUE.equals(post.getReposted()))
                .profile(ProfileDTO.ofShort(post.getProfile()))
                .textContent(post.getTextContent())
                .originalPost(post.getReposted() ? PostDTO.original(post.getOriginalPost()) : null)
                .createdAt(post.getCreatedAt())
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
