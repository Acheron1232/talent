package com.acheron.careerserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CareerServerApplication

fun main(args: Array<String>) {
    runApplication<CareerServerApplication>(*args)
}
