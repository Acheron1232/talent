package com.mykyda.talantsocials.dto;

import com.mykyda.talantsocials.database.entity.Comment;
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
public class CommentDTO {

    private UUID id;

    private ProfileDTO profile;

    private String content;

    private Boolean isAReply;

    private UUID originalCommentId;

    private Timestamp createdAt;

    public static CommentDTO of(Comment comment) {
        return CommentDTO.builder()
                .id(comment.getId())
                .profile(ProfileDTO.ofShort(comment.getProfile()))
                .content(comment.getContent())
                .isAReply(comment.getIsAReply())
                .originalCommentId(comment.getIsAReply() ? comment.getOriginalComment().getId() : null)
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
