package io.makefun.fantasticsport.core.lineup

import io.makefun.fantasticsport.core.player.PlayerPosition
import org.springframework.data.annotation.Id

class Lineup(
        @Id val id: String? = null,
        val userId: String,
        val captain: String,
        val formation: Formation = Formation.DEFAULT,
        val players: List<LineupPlayer> = emptyList()) {

    fun score(): Int = players.sumBy { it.score }
}

class LineupPlayer(val id: String,
                   var externalId: String? = null,
                   val teamId: String,
                   val name: String,
                   val position: PlayerPosition,
                   var score: Int = 0,
                   var imageUrl: String? = null)

enum class Formation { DEFAULT }