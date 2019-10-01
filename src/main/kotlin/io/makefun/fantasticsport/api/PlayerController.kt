package io.makefun.fantasticsport.api

import io.makefun.fantasticsport.api.converter.PlayerConverter
import io.makefun.fantasticsport.api.dto.PlayerResponse
import io.makefun.fantasticsport.core.player.PlayerService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/v1/players")
@RestController
class PlayerController(private val service: PlayerService,
                       private val converter: PlayerConverter) {

    private val log = LoggerFactory.getLogger(this.javaClass.name)

    @GetMapping("/{playerId}")
    fun findById(@PathVariable playerId: String): PlayerResponse {
        log.info("Find player with id: {}", playerId)
        return converter.map(service.findById(playerId))
    }
}