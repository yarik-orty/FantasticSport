package io.makefun.fantasticsport.core.lineup

import io.makefun.fantasticsport.api.dto.LineupRequest
import io.makefun.fantasticsport.core.live.RedisTemplate
import io.makefun.fantasticsport.core.player.Player
import io.makefun.fantasticsport.core.player.PlayerService
import io.makefun.fantasticsport.core.user.UserService
import io.makefun.fantasticsport.exception.LineupCaptainException
import io.makefun.fantasticsport.exception.NotFoundException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class LineupService(private val userService: UserService,
                    private val playerService: PlayerService,
                    private val redisTemplate: RedisTemplate) {

    private val log = LoggerFactory.getLogger(this.javaClass.name)

    fun create(userId: String, lineup: LineupRequest) {
        val user = userService.findById(userId)
        if (!lineup.players.contains(lineup.captain)) throw LineupCaptainException("Captain id is not in lineup")
        val players = playerService.findByIds(lineup.players)
        val lineupEntity = Lineup(userId = user.id!!,
                formation = lineup.formation, captain = lineup.captain, players = map(players))
        log.info("About to save lineup for userId: ${user.id}")
        userService.saveLineUp(lineupEntity)
    }

    fun generate(userId: String, formation: Formation): List<Player> {
        val user = userService.findById(userId)
        val allPlayers = playerService.findAll()
        val players = mutableSetOf<Player>()

        while (players.size < 5) {
            val random = allPlayers.indices.shuffled().first()
            players.add(allPlayers[random])
        }

        val lineup = Lineup(userId = user.id!!, formation = formation,
                captain = players.first().id!!, players = map(players.toList()))
        log.info("About to save lineup for userId: ${user.id}")
        // TODO: consider not to save lineup right away, but give player possibility to change it
        userService.saveLineUp(lineup)

        return players.toList()
    }

    fun findByUserId(userId: String): Lineup {
        val lineup = userService.findById(userId).lineup
                ?: throw  NotFoundException("Lineup not found for user: $userId")
        // TODO: if lineup is live go to redis
        lineup.players.forEach { it.score = redisTemplate.data[it.id]?.score ?: it.score }
        return lineup
    }

    private fun map(player: Player): LineupPlayer {
        return LineupPlayer(id = player.id!!, externalId = player.externalId,
                teamId = player.teamId, name = player.name, position = player.position)
    }

    private fun map(players: List<Player>): List<LineupPlayer> = players.map { map(it) }
}