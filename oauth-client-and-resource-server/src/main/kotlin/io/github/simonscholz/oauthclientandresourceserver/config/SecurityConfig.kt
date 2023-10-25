package io.github.simonscholz.oauthclientandresourceserver.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig {
    @Bean
    fun springFilterChain(http: HttpSecurity): SecurityFilterChain =
        http.csrf {
            it.disable()
        }
            .authorizeHttpRequests {
                it.requestMatchers(HttpMethod.GET, "/api/**").hasAnyRole(USER, ADMIN)
                it.requestMatchers(HttpMethod.GET, "/login").authenticated()
                it.requestMatchers(HttpMethod.POST, "/graphql").permitAll()
                it.anyRequest().denyAll()
            }
            .oauth2ResourceServer { oAuth2ResourceServerConfigurer ->
                oAuth2ResourceServerConfigurer.jwt {
                    it.jwtAuthenticationConverter(jwtAuthenticationConverter())
                }
            }
            .oauth2Login(Customizer.withDefaults())
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .build()

    @Bean
    fun jwtAuthenticationConverter(): Converter<Jwt, AbstractAuthenticationToken> = KeycloakRealmRoleConverter()

    companion object {
        const val ADMIN = "ADMIN"
        const val USER = "USER"
    }
}
