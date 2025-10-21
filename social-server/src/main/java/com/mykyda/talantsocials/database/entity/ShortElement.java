package com.mykyda.talantsocials.database.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShortElement extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private Type type;
    private String url;
    private Integer orderIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "short_id")
    @JsonIgnore
    @JsonBackReference
    @ToString.Exclude
    private Short shorT;

    @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant createdAt = Instant.now();

    public enum Type {
        VIDEO,IMAGE,MUSIC
    }
}
