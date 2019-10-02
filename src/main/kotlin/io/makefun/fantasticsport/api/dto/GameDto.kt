package io.makefun.fantasticsport.api.dto

import io.makefun.fantasticsport.core.game.Participant
import io.makefun.fantasticsport.core.live.PlayerEvent
import io.makefun.fantasticsport.core.game.GameStatus
import io.makefun.fantasticsport.core.game.GameType
import java.time.LocalDate

class GameRequest(var opponentId: String,
                  var opponentName: String,
                  var random: Boolean = false,
                  var name: String? = null,
                  var stake: Long)

class GameResponse(val id: String,
                   val name: String,
                   val type: GameType,
                   var status: GameStatus,
                   var startDate: LocalDate? = null,
                   var finishDate: LocalDate? = null,
                   var stake: Long = 0,
                   val participants: List<Participant> = emptyList())

class GameLineupResponse(val lineup: LineupResponse, val opponentLineup: LineupResponse)

class GameTimelineResponse(val timeline: List<PlayerEvent>, opponentTimeline: List<PlayerEvent>)