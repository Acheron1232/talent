package com.mykyda.talantsocials.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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
    private Short shorT;

    @Column(updatable = false,name = "created_at")
    private Instant createdAt =  Instant.now();

    public enum Type {
        VIDEO,IMAGE,MUSIC
    }
}
