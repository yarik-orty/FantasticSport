package io.makefun.fantasticsport.api

import io.makefun.fantasticsport.api.dto.LiveDataRequest
import io.makefun.fantasticsport.api.dto.TeamRequest
import io.makefun.fantasticsport.core.live.FeedProvider
import io.makefun.fantasticsport.core.match.Match
import io.makefun.fantasticsport.core.match.MatchService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/v1/feeds")
@RestController
class FeedProviderController(private val feedProvider: FeedProvider,
                             private val matchService: MatchService) {

    private val log = LoggerFactory.getLogger(this.javaClass.name)

    @PostMapping("/matches")
    fun matches(@RequestBody matches: List<Match>) {
        log.info("Received {} matches", matches.size)
        feedProvider.processMatches(matches)
    }

    @PostMapping("/players")
    fun players(@RequestBody teams: List<TeamRequest>) {
        log.info("Received {} teams", teams.size)
        feedProvider.processPlayers(teams)
    }

    @PostMapping("/live")
    fun live(@RequestBody liveData: LiveDataRequest) {
        val matchId = matchService.findByExternalId(liveData.externalMatchId).id!!
        when (liveData.type) {
            "STARTED" -> feedProvider.gameStarted(matchId)
            "LIVE" -> feedProvider.processLive(matchId, liveData.data)
            "FINISHED" -> feedProvider.gameFinished(matchId)
        }
    }
}