package io.makefun.fantasticsport.core.player

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("players")
data class Player(
        @Id val id: String? = null,
        var externalId: String? = null,
        val teamId: String,
        val name: String,
        val firstName: String,
        val lastName: String,
        val position: PlayerPosition,
        var country: String? = null,
        var weight: String? = null,
        var height: String? = null,
        var age: Int? = null,
        var imageUrl: String? = null)

enum class PlayerPosition { PG, SG, SF, C, PF, PG_SG, SG_SF, PG_SF, PG_PF, SF_PF, PF_C }