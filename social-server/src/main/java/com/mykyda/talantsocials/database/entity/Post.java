package com.mykyda.talantsocials.database.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.sql.Timestamp;
import java.time.Instant;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "post")
public class Post extends ContentEntity {

    @Column(nullable = false)
    @Builder.Default
    private Boolean reposted = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "original_post_id", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Post originalPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Profile profile;

    @Column
    private String textContent;

    @Column(nullable = false)
    @Builder.Default
    private Timestamp createdAt = Timestamp.from(Instant.now());
}
