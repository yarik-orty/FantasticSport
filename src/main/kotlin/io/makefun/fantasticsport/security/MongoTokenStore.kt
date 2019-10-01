package io.makefun.fantasticsport.security

import io.makefun.fantasticsport.core.user.UserService
import io.makefun.fantasticsport.security.repository.AccessToken
import io.makefun.fantasticsport.security.repository.AccessTokenRepository
import io.makefun.fantasticsport.security.repository.RefreshToken
import io.makefun.fantasticsport.security.repository.RefreshTokenRepository
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.common.OAuth2RefreshToken
import org.springframework.security.oauth2.common.util.SerializationUtils
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator
import org.springframework.security.oauth2.provider.token.TokenStore
import java.io.UnsupportedEncodingException
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

class MongoTokenStore(private val accessTokenRepository: AccessTokenRepository,
                      private val refreshTokenRepository: RefreshTokenRepository,
                      private val userService: UserService,
                      private val security: Security) : TokenStore {

    private val authenticationKeyGenerator = DefaultAuthenticationKeyGenerator()

    override fun readAuthentication(token: OAuth2AccessToken): OAuth2Authentication? {
        return readAuthentication(token.value)
    }

    override fun readAuthentication(token: String): OAuth2Authentication? {
        val tokenId = extractTokenKey(token)
        val accessToken = accessTokenRepository.findById(tokenId!!).orElse(null)
        if (accessToken != null) {
            try {
                return deserializeAuthentication(accessToken.authentication)
            } catch (e: IllegalArgumentException) {
                removeAccessToken(token)
            }

        }
        return null
    }

    override fun storeAccessToken(token: OAuth2AccessToken, authentication: OAuth2Authentication) {
        val refreshToken = token.refreshToken?.value

        if (Optional.ofNullable(readAccessToken(token.value)).isPresent) {
            removeAccessToken(token.value)
        }

        val tokenKey = extractTokenKey(token.value)

        val oAuth2AccessToken = AccessToken(
                tokenKey!!,
                serializeAccessToken(token),
                authenticationKeyGenerator.extractKey(authentication),
                authentication.name,
                authentication.oAuth2Request.clientId,
                serializeAuthentication(authentication),
                extractTokenKey(refreshToken)
        )

        accessTokenRepository.save(oAuth2AccessToken)
    }

    override fun readAccessToken(tokenValue: String): OAuth2AccessToken? {
        val tokenKey = extractTokenKey(tokenValue)
        val accessToken = accessTokenRepository.findById(tokenKey!!).orElse(null)
        if (accessToken != null) {
            try {
                return deserializeAccessToken(accessToken.token)
            } catch (e: IllegalArgumentException) {
                removeAccessToken(tokenValue)
            }
        }
        return null
    }

    override fun removeAccessToken(token: OAuth2AccessToken) {
        removeAccessToken(token.value)
    }

    override fun storeRefreshToken(oAuth2RefreshToken: OAuth2RefreshToken, oAuth2Authentication: OAuth2Authentication) {
        val tokenKey = extractTokenKey(oAuth2RefreshToken.value)
        val token = serializeRefreshToken(oAuth2RefreshToken)
        val authentication = serializeAuthentication(oAuth2Authentication)
        val refreshToken = RefreshToken(tokenKey!!, token, authentication)
        refreshTokenRepository.save(refreshToken)
    }

    override fun readRefreshToken(tokenValue: String): OAuth2RefreshToken? {
        val tokenKey = extractTokenKey(tokenValue)
        val refreshToken = refreshTokenRepository.findById(tokenKey!!).orElse(null)

        if (refreshToken != null) {
            try {
                return deserializeRefreshToken(refreshToken.token)
            } catch (e: IllegalArgumentException) {
                removeRefreshToken(tokenValue)
            }
        }

        return null
    }

    override fun readAuthenticationForRefreshToken(token: OAuth2RefreshToken): OAuth2Authentication? {
        return readAuthenticationForRefreshToken(token.value)
    }

    override fun removeRefreshToken(token: OAuth2RefreshToken) {
        removeRefreshToken(token.value)
    }

    override fun removeAccessTokenUsingRefreshToken(refreshToken: OAuth2RefreshToken) {
        removeAccessTokenUsingRefreshToken(refreshToken.value)
    }

    override fun getAccessToken(authentication: OAuth2Authentication): OAuth2AccessToken? {
        val key = authenticationKeyGenerator.extractKey(authentication)
        val accessToken = accessTokenRepository.findByAuthenticationId(key)

        if (accessToken != null) { // Logout cause there is active token
            userService.currentUser()?.let {
                security.logout(it.id!!)
            }
        }

        return null
    }

    override fun findTokensByClientIdAndUserName(clientId: String, userName: String): Collection<OAuth2AccessToken> {
        return accessTokenRepository.findByUsernameAndClientId(userName, clientId)
                .map { SerializationUtils.deserialize<OAuth2AccessToken>(it.token) }
    }

    override fun findTokensByClientId(clientId: String): Collection<OAuth2AccessToken> {
        return accessTokenRepository.findByClientId(clientId)
                .map { SerializationUtils.deserialize<OAuth2AccessToken>(it.token) }
    }

    private fun removeAccessToken(tokenValue: String) {
        val tokenKey = extractTokenKey(tokenValue)
        tokenKey?.let {
            accessTokenRepository.deleteById(it)
        }
    }

    private fun extractTokenKey(value: String?): String? {
        if (value == null) {
            return null
        }
        val digest: MessageDigest

        try {
            digest = MessageDigest.getInstance("MD5")
        } catch (e: NoSuchAlgorithmException) {
            throw IllegalStateException("MD5 algorithm not available.  Fatal (should be in the JDK).")
        }

        try {
            val bytes = digest.digest(value.toByteArray(charset("UTF-8")))
            return String.format("%032x", BigInteger(1, bytes))
        } catch (e: UnsupportedEncodingException) {
            throw IllegalStateException("UTF-8 encoding not available.  Fatal (should be in the JDK).")
        }
    }

    private fun serializeAccessToken(token: OAuth2AccessToken): ByteArray {
        return SerializationUtils.serialize(token)
    }

    private fun serializeRefreshToken(token: OAuth2RefreshToken): ByteArray {
        return SerializationUtils.serialize(token)
    }

    private fun serializeAuthentication(authentication: OAuth2Authentication): ByteArray {
        return SerializationUtils.serialize(authentication)
    }

    private fun deserializeAccessToken(token: ByteArray): OAuth2AccessToken {
        return SerializationUtils.deserialize(token)
    }

    private fun deserializeRefreshToken(token: ByteArray): OAuth2RefreshToken {
        return SerializationUtils.deserialize(token)
    }

    private fun deserializeAuthentication(authentication: ByteArray): OAuth2Authentication {
        return SerializationUtils.deserialize(authentication)
    }

    private fun readAuthenticationForRefreshToken(value: String): OAuth2Authentication? {
        val tokenId = extractTokenKey(value)
        val refreshToken = refreshTokenRepository.findById(tokenId!!).orElse(null)
        if (refreshToken != null) {
            try {
                return deserializeAuthentication(refreshToken.authentication)
            } catch (e: IllegalArgumentException) {
                removeRefreshToken(value)
            }
        }
        return null
    }

    private fun removeAccessTokenUsingRefreshToken(refreshToken: String) {
        val tokenId = extractTokenKey(refreshToken)
        tokenId?.let {
            accessTokenRepository.deleteByRefreshToken(it)
        }
    }

    private fun removeRefreshToken(token: String) {
        val tokenId = extractTokenKey(token)
        tokenId?.let {
            refreshTokenRepository.deleteById(it)
        }
    }
}
