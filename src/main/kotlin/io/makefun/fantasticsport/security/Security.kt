package io.makefun.fantasticsport.security

import io.makefun.fantasticsport.core.user.UserService
import io.makefun.fantasticsport.security.loggin.LoggingService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Lazy
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.security.web.authentication.WebAuthenticationDetails
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.util.*

@Component
class Security(private val service: UserService,
               private val loggingService: LoggingService,
               @param:Lazy private val tokenStore: TokenStore,
               @param:Value("\${security.auth.clientId}")
               private val clientId: String) {

    private val log = LoggerFactory.getLogger(this.javaClass.name)

    fun logout(userId: String) {
        val user = service.findById(userId)
        val email = user.email
        log.debug("About to logout user with id={}, email={}", userId, email)
        val tokens = tokenStore.findTokensByClientIdAndUserName(clientId, email)
        tokens.forEach { token ->
            tokenStore.removeAccessToken(token)
            tokenStore.removeRefreshToken(token.refreshToken)
        }
        loggingService.logout(userId)
        log.debug("User with id={}, email={} has been logged out", userId)
    }

    companion object {

        const val RESOURCE_ID = "fantastic_sport"
        private const val SYSTEM_ACCOUNT = "system"

        fun currentUserLogin(): String {
            val securityContext = SecurityContextHolder.getContext()
            val authentication = securityContext.authentication
            var userName: String? = null
            if (authentication != null) {
                if (authentication.principal is UserDetails) {
                    val user = authentication.principal as UserDetails
                    userName = user.username
                } else if (authentication.principal is String) {
                    userName = authentication.principal as String
                }
            }
            return userName ?: SYSTEM_ACCOUNT
        }

        fun getIpAddress(): String {
            return Optional.ofNullable(SecurityContextHolder.getContext())
                    .map { it.authentication }
                    .map { it.details }
                    .filter { WebAuthenticationDetails::class.java.isInstance(it) }
                    .map { WebAuthenticationDetails::class.java.cast(it) }
                    .map { it.remoteAddress }
                    .orElseGet { ipAddressFallback() }
        }

        private fun ipAddressFallback(): String {
            return Optional.ofNullable(RequestContextHolder.currentRequestAttributes())
                    .filter { ServletRequestAttributes::class.java.isInstance(it) }
                    .map { ServletRequestAttributes::class.java.cast(it) }
                    .map { it.request }
                    .map { it.remoteAddr }
                    .orElse(null)
        }
    }
}
