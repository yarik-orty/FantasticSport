package io.makefun.fantasticsport.api

import io.makefun.fantasticsport.api.converter.PlayerConverter
import io.makefun.fantasticsport.api.converter.UserConverter
import io.makefun.fantasticsport.api.dto.LineupRequest
import io.makefun.fantasticsport.api.dto.LineupResponse
import io.makefun.fantasticsport.api.dto.PlayerResponse
import io.makefun.fantasticsport.core.lineup.Formation
import io.makefun.fantasticsport.core.lineup.LineupService
import io.makefun.fantasticsport.security.AuthUser
import org.slf4j.LoggerFactory
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RequestMapping("/v1/lineups")
@RestController
class LineupController(private val service: LineupService,
                       private val converter: PlayerConverter,
                       private val userConverter: UserConverter) {

    private val log = LoggerFactory.getLogger(this.javaClass.name)

    @PostMapping("/create")
    fun create(@AuthenticationPrincipal authUser: AuthUser, @RequestBody lineup: LineupRequest) {
        log.info("Create lineup for user: {} with formation: {}", authUser.id, lineup.formation)
        service.create(authUser.id, lineup)
    }

    @GetMapping("/generate")
    fun generate(@AuthenticationPrincipal authUser: AuthUser): List<PlayerResponse> {
        log.info("Generate lineup for user: {}", authUser.id)
        return converter.map(service.generate(authUser.id, Formation.DEFAULT))
    }

    @GetMapping
    fun findByUserId(@AuthenticationPrincipal authUser: AuthUser): LineupResponse? {
        return userConverter.convert(service.findByUserId(authUser.id))
    }
}