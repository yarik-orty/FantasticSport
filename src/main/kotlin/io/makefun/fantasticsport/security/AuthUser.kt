package io.makefun.fantasticsport.security

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User

class AuthUser(val id: String,
               username: String,
               password: String,
               roles: Collection<UserRole>) : User(username, password, authorities(roles)) {

    companion object {
        fun authorities(roles: Collection<UserRole>) = roles.map { SimpleGrantedAuthority(it.role) }
    }
}

enum class UserRole(val role: String) {

    USER("USER"),
    ADMIN("ADMIN"),
    SUPER_ADMIN("SUPER_ADMIN");

    companion object {
        const val ROLE_PREFIX = "ROLE_"
    }
}