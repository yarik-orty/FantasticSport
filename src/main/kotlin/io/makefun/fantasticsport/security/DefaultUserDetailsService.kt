package io.makefun.fantasticsport.security

import io.makefun.fantasticsport.core.user.UserService
import io.makefun.fantasticsport.exception.NotFoundException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import java.util.*

@Service
class DefaultUserDetailsService(private val service: UserService) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        return Optional.ofNullable(service.findByUsername(username))
                .map { AuthUser(it.id!!, it.email, it.password!!, it.roles) }
                .orElseThrow { NotFoundException("User $username not found") }
    }
}