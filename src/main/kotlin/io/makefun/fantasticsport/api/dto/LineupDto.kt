package io.makefun.fantasticsport.api.dto

import io.makefun.fantasticsport.core.lineup.Formation
import io.makefun.fantasticsport.core.lineup.LineupPlayer

class LineupRequest(var formation: Formation,
                    var players: List<String>,
                    var captain: String)

class LineupResponse(var id: String? = null,
                     var userId: String,
                     var players: List<LineupPlayer>,
                     var formation: Formation,
                     var captain: String)