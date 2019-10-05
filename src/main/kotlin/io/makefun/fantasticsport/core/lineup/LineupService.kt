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
        // TODO: validation for players and lineup
        if (!lineup.players.contains(lineup.captain)) throw LineupCaptainException("Captain id is not in lineup")
        val user = userService.findById(userId)
        val players = playerService.findByIds(lineup.players)
        val lineupEntity = Lineup(userId = user.id!!,
                formation = lineup.formation, captain = lineup.captain, players = map(players))
        log.info("About to save lineup for userId: ${user.id}")
        userService.saveLineUp(lineupEntity)
    }

    fun generate(userId: String, formation: Formation): List<Player> {
        val user = userService.findById(userId)
        val players = playerService.findAll()
        val lineupPlayers = mutableSetOf<Player>()

        while (lineupPlayers.size < MIN_PLAYERS) {
            val random = players.indices.shuffled().first()
            lineupPlayers.add(players[random])
        }

        val lineup = Lineup(userId = user.id!!, formation = formation,
                captain = lineupPlayers.first().id!!, players = map(lineupPlayers.toList()))

        log.info("About to save lineup for userId: ${user.id}")
        userService.saveLineUp(lineup)

        return lineupPlayers.toList()
    }

    fun findByUserId(userId: String): Lineup {
        val lineup = userService.findById(userId).lineup
                ?: throw  NotFoundException("Lineup not found for user: $userId")
        // Only if lineup is live go to Redis
        lineup.players.forEach { it.score = redisTemplate.data[it.id]?.score ?: it.score }
        return lineup
    }

    private fun map(players: List<Player>): List<LineupPlayer> = players.map { map(it) }

    private fun map(player: Player): LineupPlayer = LineupPlayer(player.id!!, player.externalId, player.teamId, player.name, player.position)

    companion object {

        private const val MIN_PLAYERS = 5
    }
}