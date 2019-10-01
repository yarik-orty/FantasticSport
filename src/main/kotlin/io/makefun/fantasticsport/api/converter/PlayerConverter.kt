package io.makefun.fantasticsport.api.converter

import io.makefun.fantasticsport.api.dto.PlayerRequest
import io.makefun.fantasticsport.api.dto.PlayerResponse
import io.makefun.fantasticsport.core.player.Player
import org.springframework.stereotype.Component

@Component
class PlayerConverter {

    fun convert(players: List<PlayerRequest>): List<Player> {
        return players.map { convert(it) }
    }

    fun map(players: List<Player>): List<PlayerResponse> {
        return players.map { map(it) }
    }

    fun convert(player: PlayerRequest): Player {
        return Player(
                externalId = player.externalId,
                teamId = player.teamId,
                name = player.name,
                firstName = player.firstName ?: "",
                lastName = player.lastName ?: "",
                position = player.position,
                country = player.country,
                weight = player.weight,
                height = player.height,
                age = player.age)
    }

    fun map(player: Player): PlayerResponse {
        return PlayerResponse(
                id = player.id!!,
                teamId = player.teamId,
                name = player.name,
                firstName = player.firstName,
                lastName = player.lastName,
                position = player.position,
                country = player.country,
                weight = player.weight,
                height = player.height,
                age = player.age,
                imageUrl = player.imageUrl)
    }
}