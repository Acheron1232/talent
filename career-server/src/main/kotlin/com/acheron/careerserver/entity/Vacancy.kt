package com.acheron.careerserver.entity

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import java.util.UUID

@Entity
data class Vacation(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID = UUID.randomUUID(),
    var title: String,
    /**
    `Markdown format`
     **/
    var description: String,
    var tag: MutableList<String> = mutableListOf(),
    @OneToMany(mappedBy = "vacation")
    var applies: MutableList<Apply> = mutableListOf(),
    @ManyToOne
    @JoinColumn(name = "company_id")
    var company: Company,
)