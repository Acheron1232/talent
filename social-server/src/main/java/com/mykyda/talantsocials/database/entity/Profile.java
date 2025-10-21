package com.mykyda.talantsocials.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mykyda.talantsocials.database.enums.ProfileStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Entity
@Table(name = "profile")
@AllArgsConstructor
@NoArgsConstructor
@Builder
//@JsonIdentityInfo(
//        generator = ObjectIdGenerators.PropertyGenerator.class,
//        property = "id"
//)
public class Profile {

    @Id
    @Column(unique = true, nullable = false)
    private Long id;

    @Column(unique = true, nullable = false)
    private String tag;

    //Address !!

    @Column(nullable = false)
    private String displayName;

    @Column
    private String profilePictureUrl;

    @Column
    private String bannerPictureUrl;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ProfileStatus status = ProfileStatus.NEWBIE;

    @Lob
    @Column(columnDefinition = "text")
    private String bioMarkdown;

    @Column(nullable = false)
    @Builder.Default
    private Long followersAmount = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Long followingAmount = 0L;

//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "profile", orphanRemoval = true)
//    private List<LanguageSkill> languageSkills;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "profile")
    private PostPreference postPreference;

//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "profile", orphanRemoval = true)
//    private List<JobSkill> jobsSkills;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "profile")
    @JsonIgnore
    @ToString.Exclude
    private List<Post> posts;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "profile")
    @JsonIgnore
    @ToString.Exclude
    private List<Short> shorts;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "profile")
    @JsonIgnore
    @ToString.Exclude
    private List<Like> likes;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "profile")
    @JsonIgnore
    @ToString.Exclude
    private List<Comment> comments;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "follower")
    @JsonIgnore
    @ToString.Exclude
    private List<Follow> follows;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "followed")
    @JsonIgnore
    @ToString.Exclude
    private List<Follow> followed;
}