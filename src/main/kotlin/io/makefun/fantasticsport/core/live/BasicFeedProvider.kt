package io.makefun.fantasticsport.core.live

import io.makefun.fantasticsport.api.converter.PlayerConverter
import io.makefun.fantasticsport.api.dto.TeamRequest
import io.makefun.fantasticsport.core.match.Match
import io.makefun.fantasticsport.core.game.GameService
import io.makefun.fantasticsport.core.match.MatchService
import io.makefun.fantasticsport.core.player.PlayerService
import org.springframework.stereotype.Component

@Component
class BasicFeedProvider(private val playerService: PlayerService,
                        private val matchService: MatchService,
                        private val gameService: GameService,
                        private val redisTemplate: RedisTemplate,
                        private val playerConverter: PlayerConverter) : FeedProvider {

    override fun processMatches(matches: List<Match>) {
        matchService.save(matches)
    }

    override fun processPlayers(teams: List<TeamRequest>) {
        teams.forEach { it.players.forEach { player -> player.teamId = it.externalId } }
        val players = teams.flatMap { it.players }.map { playerConverter.convert(it) }
        playerService.save(players)
    }

    override fun processLive(matchId: String, events: List<MatchEvent>) {
        events.forEach {
            val player = redisTemplate.data[it.playerId] ?: RedisPlayer(it.playerId, 0)
            redisTemplate.data[it.playerId] = player.aggregate(it.event)
        }
    }

    override fun gameStarted(matchId: String) {
        val match = matchService.findById(matchId)
        gameService.start(matchId)
    }

    override fun gameFinished(matchId: String) {
        val match = matchService.findById(matchId)
        gameService.finish(matchId)
    }
}