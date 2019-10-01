package io.makefun.fantasticsport.api

import io.makefun.fantasticsport.api.converter.UserConverter
import io.makefun.fantasticsport.api.dto.UserRequest
import io.makefun.fantasticsport.api.dto.UserResponse
import io.makefun.fantasticsport.core.user.UserService
import org.slf4j.LoggerFactory
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
    fun signUp(@RequestBody user: UserRequest) {
        log.info("Sign-up user with name {}", user.name)
        service.signUp(converter.convert(user))
    }
}