package io.makefun.fantasticsport.core.game

import io.makefun.fantasticsport.core.user.User
import io.makefun.fantasticsport.exception.BadRequestException
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document("games")
data class Game(
        @Id val id: String? = null,
        val name: String,
        val type: GameType,
        var status: GameStatus,
        var random: Boolean,
        var date: LocalDate,
        var finishDate: LocalDate? = null,
        var stake: Long = 0,// TODO: move to participant?
        val participants: MutableList<Participant> = mutableListOf(),
        val tags: MutableSet<String> = mutableSetOf()) {

    fun join(user: User) {
        val participant = participants.find { it.userId == user.id }
        if (participant?.accepted == true) throw BadRequestException("User already joined")
        if (participant != null) {
            participant.accepted = true
        } else {
            participants.add(Participant(user.id!!, user.name, accepted = true, owner = false))
        }
        status = GameStatus.ACCEPTED
    }

    fun start() {
        status = GameStatus.LIVE
    }

    fun finish() {
        status = GameStatus.PAST
        finishDate = LocalDate.now()
    }
}

enum class GameStatus {
    LIVE, ACCEPTED, PENDING, PAST, NOT_STARTED, NOT_FINISHED
}

enum class GameType {
    SINGLE
}

class Participant(val userId: String, val name: String,
                  var accepted: Boolean, val owner: Boolean) {
    @Transient var score: Int = 0
}