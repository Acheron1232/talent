package com.acheron.careerserver.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id




@Entity
class Company(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var name: String,
    var description: String,
    var website: String,
    var logo: String,
    var vacations: MutableList<Vacation>,
    var hrs: MutableList<Hr>,
    var ceo:User = getUser()
) {

    fun getUser(): User {
        return User()
    }
}

