package io.makefun.fantasticsport.api.dto

import io.makefun.fantasticsport.core.player.PlayerPosition

class TeamRequest(var externalId: String,
                  var teamName: String,
                  var players: List<PlayerRequest> = emptyList())

class PlayerRequest(var externalId: String? = null,
                    var teamId: String = "",
                    var name: String,
                    var firstName: String? = null,
                    var lastName: String? = null,
                    var position: PlayerPosition,
                    var country: String? = null,
                    var weight: String? = null,
                    var height: String? = null,
                    var age: Int? = null)

class PlayerResponse(val id: String,
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
