package io.github.simonscholz.oauthclientandresourceserver.api

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class LoginController {
    @GetMapping("/login")
    fun hello(): ResponseEntity<String> = ResponseEntity.ok("LoggedIn")
}
