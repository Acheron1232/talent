package com.mykyda.talantsocials.database.entity;

import com.mykyda.talantsocials.database.id.FollowId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.sql.Timestamp;
import java.time.Instant;

@Data
@Entity
@Builder
@Table(name = "follow")
@NoArgsConstructor
@AllArgsConstructor
public class Follow {

    @EmbeddedId
    private FollowId id;

    @MapsId("followerId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "follower_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Profile follower;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("followedId")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "followed_id")
    private Profile followed;

    @Column
    @Builder.Default
    private Timestamp createdAt = Timestamp.from(Instant.now());
}
