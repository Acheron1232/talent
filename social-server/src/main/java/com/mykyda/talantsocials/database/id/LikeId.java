package com.mykyda.talantsocials.database.id;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class LikeId implements Serializable {
    private UUID postId;
    private UUID profileId;
}
