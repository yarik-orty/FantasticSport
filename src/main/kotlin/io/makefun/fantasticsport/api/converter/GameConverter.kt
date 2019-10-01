package io.makefun.fantasticsport.api.converter

import io.makefun.fantasticsport.api.dto.GameResponse
import io.makefun.fantasticsport.core.game.Game
import org.springframework.stereotype.Component

@Component
class GameConverter {

    fun convert(games: List<Game>): List<GameResponse> {
        return games.map { convert(it) }
    }

    fun convert(game: Game): GameResponse {
        return GameResponse(
                id = game.id!!,
                name = game.name,
                status = game.status,
                type = game.type,
                date = game.date,
                stake = game.stake,
                participants = game.participants)
    }
}