package io.makefun.fantasticsport.core.live

import io.makefun.fantasticsport.api.dto.TeamRequest
import io.makefun.fantasticsport.core.match.Match

interface FeedProvider {

    fun processMatches(matches: List<Match>)

    fun processPlayers(teams: List<TeamRequest>)

    fun processLive(matchId: String, events: List<MatchEvent>)

    fun gameStarted(matchId: String)

    fun gameFinished(matchId: String)
}