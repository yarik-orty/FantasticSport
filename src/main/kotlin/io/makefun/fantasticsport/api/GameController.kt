package io.makefun.fantasticsport.api

import io.makefun.fantasticsport.api.converter.GameConverter
import io.makefun.fantasticsport.api.converter.UserConverter
import io.makefun.fantasticsport.api.dto.GameLineupResponse
import io.makefun.fantasticsport.api.dto.GameRequest
import io.makefun.fantasticsport.api.dto.GameResponse
import io.makefun.fantasticsport.api.dto.GameTimelineResponse
import io.makefun.fantasticsport.core.game.GameService
import io.makefun.fantasticsport.core.game.GameStatus
import io.makefun.fantasticsport.security.AuthUser
import org.slf4j.LoggerFactory
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RequestMapping("/v1/games")
@RestController
class GameController(private val service: GameService,
                     private val converter: GameConverter,
                     private val userConverter: UserConverter) {

    private val log = LoggerFactory.getLogger(this.javaClass.name)

    @GetMapping
    fun findByUserId(@AuthenticationPrincipal authUser: AuthUser,
                     @RequestParam(required = false) status: GameStatus?): List<GameResponse> {
        log.info("Find games for user: {}", authUser.id)
        return converter.convert(service.findByUserId(authUser.id, status))
    }

    @PostMapping("/create")
    fun create(@AuthenticationPrincipal authUser: AuthUser, @RequestBody games: List<GameRequest>) {
        log.info("Create single game")
        service.create(authUser.id, games)
    }

    @PostMapping("/join")
    fun join(@AuthenticationPrincipal authUser: AuthUser, @RequestParam gameIds: List<String>) {
        log.info("Join to game with id: {}", gameIds.toString())
        service.join(authUser.id, gameIds)
    }

    @GetMapping("/available")
    fun findAvailable(@AuthenticationPrincipal authUser: AuthUser): List<GameResponse> {
        log.info("Find available games")
        return converter.convert(service.findAvailable(authUser.id))
    }

    @GetMapping("/random")
    fun findRandom(@AuthenticationPrincipal authUser: AuthUser): List<GameResponse> {
        log.info("Find available games")
        return converter.convert(service.findRandom(authUser.id))
    }

    @GetMapping("/{gameId}")
    fun findById(@PathVariable gameId: String): GameResponse {
        log.info("Find game for gameId: {}", gameId)
        return converter.convert(service.findById(gameId))
    }

    @GetMapping("/timeline")
    fun gameTimeline(@AuthenticationPrincipal authUser: AuthUser, @RequestParam gameId: String): GameTimelineResponse {
        log.info("Find timeline events game for gameId: {}", gameId)
        return service.gameTimeline(authUser.id, gameId)
    }

    @GetMapping("/lineup")
    fun gameLineup(@AuthenticationPrincipal authUser: AuthUser, @RequestParam gameId: String): GameLineupResponse {
        log.info("Find lineups for gameId: {}", gameId)
        val lineups = service.gameLineup(authUser.id, gameId)
        return GameLineupResponse(userConverter.convert(lineups.first)!!, userConverter.convert(lineups.second)!!)
    }
}