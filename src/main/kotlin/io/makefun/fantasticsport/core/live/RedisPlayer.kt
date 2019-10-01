package io.makefun.fantasticsport.core.live

class RedisPlayer(val playerId: String,
                  var score: Int,
                  val events: MutableList<PlayerEvent> = mutableListOf()) {

    fun aggregate(event: PlayerEvent): RedisPlayer {
        this.score += event.score()
        this.events.add(event)
        return this
    }

    private fun score(): Int {
        return events.sumBy { it.score() }
    }
}