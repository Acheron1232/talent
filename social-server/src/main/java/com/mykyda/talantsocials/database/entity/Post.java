package com.mykyda.talantsocials.database.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
@Table(name = "post")
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
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

    @Column(nullable = false)
    @Builder.Default
    private Integer likesAmount = 0;
}
