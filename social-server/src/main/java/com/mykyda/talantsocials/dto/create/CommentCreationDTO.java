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
public class CommentCreationDTO {

    UUID id;

    UUID profileId;

    UUID postId;

    @JsonProperty("isAReply")
    boolean isAReply;

    UUID originalCommentId;

    String content;
}
