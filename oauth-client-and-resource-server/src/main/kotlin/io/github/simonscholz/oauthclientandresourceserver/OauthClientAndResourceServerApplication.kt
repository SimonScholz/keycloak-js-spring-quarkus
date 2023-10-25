package io.github.simonscholz.oauthclientandresourceserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class OauthClientAndResourceServerApplication

fun main(args: Array<String>) {
	runApplication<OauthClientAndResourceServerApplication>(*args)
}
