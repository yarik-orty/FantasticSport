package io.makefun.fantasticsport.security.provider

import io.makefun.fantasticsport.core.user.UserService
import io.makefun.fantasticsport.exception.NotFoundException
import io.makefun.fantasticsport.security.AuthUser
import org.slf4j.LoggerFactory
import org.springframework.context.support.MessageSourceAccessor
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class BasicAuthenticationProvider(private val service: UserService,
                                  private val encoder: PasswordEncoder,
                                  private val messages: MessageSourceAccessor) : AuthenticationProvider {

    private val log = LoggerFactory.getLogger(this.javaClass.name)

    override fun authenticate(authentication: Authentication): Authentication? {
        val authPassword = authentication.credentials as String?
        val authName = authentication.name.toLowerCase()
        if (authPassword === null) {
            return null // skip to next authentication provider
        }

        val user = service.findByUsername(authName)
                ?: throw BadCredentialsException(messages.getMessage("auth.invalid.creds", "Bad credentials"))

        if (user.password?.isEmpty() == true || !encoder.matches(authPassword, user.password)) {
            throw BadCredentialsException(messages.getMessage("auth.invalid.creds", "Bad credentials"))
        }

        if (user.email.isEmpty()) {
            throw NotFoundException(messages.getMessage("auth.email.not.found", "Email not found"))
        }

        val securityUser = AuthUser(user.id!!, user.email, user.password!!, user.roles)
        val auth = UsernamePasswordAuthenticationToken(securityUser, user.password, securityUser.authorities)

        SecurityContextHolder.getContext().authentication = auth

        return auth
    }

    override fun supports(authentication: Class<*>): Boolean {
        return UsernamePasswordAuthenticationToken::class.java.isAssignableFrom(authentication)
    }
}
