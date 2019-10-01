package io.makefun.fantasticsport.core.match

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("teams")
data class Team(
        @Id val id: String? = null,
        val externalId: String,
        val name: String)