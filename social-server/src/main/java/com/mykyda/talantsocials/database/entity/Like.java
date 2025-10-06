package com.mykyda.talantsocials.database.entity;

import com.mykyda.talantsocials.database.id.LikeId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Data
@Entity
@Builder
@Table(name = "likes")
@NoArgsConstructor
@AllArgsConstructor
public class Like {

    @EmbeddedId
    private LikeId id;

    @MapsId("contentEntityId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "content_entity_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ContentEntity contentEntity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("profileId")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    @Builder.Default
    private Instant createdAt = Instant.now();
}
