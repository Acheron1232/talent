package com.mykyda.talantsocials.database.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
import java.util.List;
import java.util.UUID;

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
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Profile profile;

    @Column
    @Builder.Default
    private Boolean reposted = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "original_post_id", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Post originalPost;

    @Column
    private String textContent;

    @Column
    @Builder.Default
    private Timestamp createdAt = Timestamp.from(Instant.now());

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "post")
    @JsonIgnore
    private List<Like> likes;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "post")
    @JsonIgnore
    private List<Comment> comments;
}
