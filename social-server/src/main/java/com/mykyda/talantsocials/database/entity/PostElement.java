package com.mykyda.talantsocials.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostElement extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private Type type;

    private String url;

    private Integer orderIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    @JsonIgnore
    private Post post;

    @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    @Builder.Default
    private Instant createdAt = Instant.now();

    public enum Type {
        VIDEO,IMAGE,MUSIC
    }
}
