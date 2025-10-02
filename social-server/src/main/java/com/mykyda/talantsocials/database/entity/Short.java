package com.mykyda.talantsocials.database.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Short extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "profile_id")
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

    private Long likes = 0L;

    private Long views = 0L;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_public")
    private Boolean isPublic;

    //TODO
//    @OneToMany(mappedBy = "short")
//    private List<Comment> comments;

    @Column(updatable = false,name = "created_at")
    private Instant createdAt = Instant.now();

    public enum Type {
        VIDEO, IMAGES
    }
}
