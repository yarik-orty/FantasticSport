package io.makefun.fantasticsport.security.repository

import org.springframework.data.mongodb.repository.MongoRepository

interface AccessTokenRepository : MongoRepository<AccessToken, String> {

    fun deleteByRefreshToken(refreshToken: String): Long

    fun findByAuthenticationId(authId: String): AccessToken?

    fun findByUsernameAndClientId(username: String, clientId: String): List<AccessToken>

    fun findByClientId(clientId: String): List<AccessToken>
}
