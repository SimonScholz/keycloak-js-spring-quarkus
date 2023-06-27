package io.github.simonscholz.securedspringbackend.api

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("/api")
class UserController {

    @GetMapping("/hello")
    fun hello(): String = "Hello World!"

    @GetMapping("/me")
    fun me(principal: Principal): String = "Hello ${principal.name}"
}
