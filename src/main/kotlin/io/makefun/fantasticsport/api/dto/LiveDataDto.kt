package io.makefun.fantasticsport.api.dto

import io.makefun.fantasticsport.core.live.MatchEvent

class LiveDataRequest(
        val externalMatchId: String,
        val type: String,
        val data: List<MatchEvent>)

