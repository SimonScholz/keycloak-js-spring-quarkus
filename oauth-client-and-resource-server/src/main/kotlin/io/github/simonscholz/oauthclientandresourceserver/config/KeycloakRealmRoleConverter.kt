package io.github.simonscholz.oauthclientandresourceserver.config

import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter

/**
 * Based on https://medium.com/geekculture/using-keycloak-with-spring-boot-3-0-376fa9f60e0b
 */
internal class KeycloakRealmRoleConverter : Converter<Jwt, AbstractAuthenticationToken> {
    override fun convert(jwt: Jwt): AbstractAuthenticationToken {
        val grantedAuthorities = grantedAuthorities(jwt).plus(jwtGrantedAuthoritiesConverter.convert(jwt)!!)
        return JwtAuthenticationToken(jwt, grantedAuthorities, getPrincipalClaimName(jwt))
    }

    private fun getPrincipalClaimName(jwt: Jwt): String = jwt.getClaim("preferred_username")

    private fun grantedAuthorities(jwt: Jwt): Collection<GrantedAuthority> {
        val resourceAccessClaim = jwt.getClaim<Map<String, Any>>("resource_access")

        return resourceAccessClaim?.let { resourceAccess ->
            resourceAccess["vuejs"]?.let { vueResource ->
                val resource: Map<String, Any> = vueResource as Map<String, Any>
                val roles: Collection<String> = resource["roles"] as Collection<String>
                roles.map { "ROLE_${it.uppercase()}" }.map(::SimpleGrantedAuthority)
            }
        } ?: setOf()
    }

    companion object {
        private val jwtGrantedAuthoritiesConverter = JwtGrantedAuthoritiesConverter()
    }
}
