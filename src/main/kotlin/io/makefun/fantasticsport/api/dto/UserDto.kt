package io.makefun.fantasticsport.api.dto

import io.makefun.fantasticsport.core.user.UserTeam

class UserRequest(val email: String,
                  val password: String,
                  val name: String,
                  val team: UserTeam)

class UserResponse(val id: String,
                   val email: String,
                   val name: String,
                   val team: UserTeam,
                   val score: Long,
                   val coins: Long,
                   var lineup: LineupResponse? = null)