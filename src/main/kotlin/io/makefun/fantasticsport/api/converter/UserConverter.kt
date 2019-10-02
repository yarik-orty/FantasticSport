package io.makefun.fantasticsport.api.converter

import io.makefun.fantasticsport.api.dto.LineupResponse
import io.makefun.fantasticsport.api.dto.SignUpUser
import io.makefun.fantasticsport.api.dto.UserResponse
import io.makefun.fantasticsport.core.lineup.Lineup
import io.makefun.fantasticsport.core.user.User
import io.makefun.fantasticsport.core.user.UserTeam
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class UserConverter(private val encoder: PasswordEncoder) {

    fun convert(user: SignUpUser): User {
        return User(
                email = user.email,
                password = encoder.encode(user.password),
                name = "",
                team = UserTeam(""))
    }

    fun convert(user: User): UserResponse {
        return UserResponse(
                id = user.id!!,
                email = user.email,
                name = user.name,
                team = user.team,
                score = user.score,
                coins = user.wallet.amount,
                lineup = convert(user.lineup))
    }

    fun convert(lineup: Lineup?): LineupResponse? {
        if (lineup == null) return null
        return LineupResponse(
                userId = lineup.userId,
                formation = lineup.formation,
                captain = lineup.captain,
                players = lineup.players
        )
    }
}