package io.makefun.fantasticsport.core.match

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document("matches")
data class Match(
        @Id val id: String? = null,
        val externalId: String,
        val date: LocalDate,
        val homeTeam: String,
        val awayTeam: String)