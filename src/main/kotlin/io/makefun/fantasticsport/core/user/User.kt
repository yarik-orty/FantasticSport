package io.makefun.fantasticsport.core.user

import io.makefun.fantasticsport.core.lineup.Lineup
import io.makefun.fantasticsport.security.UserRole
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("users")
data class User(
        @Id val id: String? = null,
        val email: String,
        val number: String? = null,
        val password: String? = null,
        val name: String,
        val team: UserTeam,
        var score: Long = 0,
        val wallet: Wallet = Wallet(),
        val lineups: MutableList<Lineup> = mutableListOf(),
        val roles: MutableList<UserRole> = mutableListOf(UserRole.USER)) {

    var lineup: Lineup?
        get() = lineups.firstOrNull()
        set(value) {
            if (value == null) {
                lineups.clear()
                return
            }
            if (lineups.isEmpty()) lineups.add(value) else lineups[0] = value
        }
}

data class UserTeam(val name: String,
                    val colors: List<String> = emptyList())

data class Wallet(var amount: Long = 0,
                  val reserveBucket: MutableMap<String, Long> = mutableMapOf(),
                  val currency: Currency = Currency.COIN)

enum class Currency { COIN }