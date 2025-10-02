package com.mykyda.talantsocials.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Inheritance(strategy = InheritanceType.JOINED)
@Entity
@Table(name = "content_entity")
public abstract class ContentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

//    @Column(nullable = false)
//    @Enumerated(EnumType.STRING)
//    private UserContentType type;

    @OneToMany(mappedBy = "contentEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Comment> comments;

}
