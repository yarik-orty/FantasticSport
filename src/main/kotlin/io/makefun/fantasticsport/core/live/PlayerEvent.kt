package io.makefun.fantasticsport.core.live

import java.time.LocalDate

class PlayerEvent(val date: LocalDate, val events: MutableMap<Event, Int> = mutableMapOf()) {

    fun score(): Int {
        return events.entries.sumBy { entry -> entry.key.points * entry.value }
    }

    enum class Event(val eventName: String, val points: Int) {

        GOAL("goal", 5),
        HIT("hit", 2),
        PASS("pass", 1),
        FOL("fol", -1),
    }
}
