package io.makefun.fantasticsport.api

import io.makefun.fantasticsport.api.converter.UserConverter
import io.makefun.fantasticsport.api.dto.SignUpUser
import io.makefun.fantasticsport.api.dto.UserRequest
import io.makefun.fantasticsport.api.dto.UserResponse
import io.makefun.fantasticsport.core.user.UserService
import io.makefun.fantasticsport.security.AuthUser
import org.slf4j.LoggerFactory
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RequestMapping("/v1/users")
@RestController
class UserController(private val service: UserService,
                     private val converter: UserConverter) {

    private val log = LoggerFactory.getLogger(this.javaClass.name)

    @GetMapping
    fun findByTeam(@RequestParam team: String): UserResponse {
        log.info("Find user for team: {}", team)
        return converter.convert(service.findByTeamName(team))
    }

    @PostMapping("/sign-up")
    fun signUp(@RequestBody user: SignUpUser) {
        log.info("Sign-up user with email {}", user.email)
        service.signUp(converter.convert(user))
    }

    @PatchMapping
    fun finishRegistration(@AuthenticationPrincipal authUser: AuthUser, @RequestBody user: UserRequest) {
        log.info("About to update user with id:", authUser.id)
        service.finishRegistration(authUser.id, user)
    }
}