package io.github.simonscholz.securedspringbackend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SecuredSpringBackendApplication

fun main(args: Array<String>) {
    runApplication<SecuredSpringBackendApplication>(*args)
}
