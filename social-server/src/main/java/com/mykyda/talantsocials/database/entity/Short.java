package com.mykyda.talantsocials.database.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "short")
public class Short extends ContentEntity {

    @ManyToOne
    @JoinColumn(name = "profile_id")
    @EqualsAndHashCode.Exclude
    private Profile profile;

    @Enumerated(EnumType.STRING)
    private Type type;

    @OneToMany(mappedBy = "shorT", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShortElement> elements;

    @ManyToMany
    @JoinTable(
            name = "short_tag",
            joinColumns = @JoinColumn(name = "short_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags = new ArrayList<>();

    private Long views = 0L;

//    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_public")
    private Boolean isPublic;

    @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    @Builder.Default
    private Instant createdAt = Instant.now();

    public enum Type {
        VIDEO, IMAGES
    }
}
