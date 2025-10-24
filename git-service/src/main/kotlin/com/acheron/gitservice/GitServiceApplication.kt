package com.acheron.gitservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GitServiceApplication

fun main(args: Array<String>) {
    runApplication<GitServiceApplication>(*args)
}
