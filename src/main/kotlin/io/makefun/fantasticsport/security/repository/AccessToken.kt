package io.makefun.fantasticsport.security.repository

import io.makefun.fantasticsport.base.Entity
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.PersistenceConstructor
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = AccessToken.COLLECTION_NAME)
class AccessToken @PersistenceConstructor constructor(
        @Id val _id: String,
        val token: ByteArray,
        val authenticationId: String,
        val username: String,
        val clientId: String,
        val authentication: ByteArray,
        val refreshToken: String? = null) : Entity() {

    override fun getId(): String? {
        return _id
    }

    override val collectionName: String get() = COLLECTION_NAME

    override fun hashCode(): Int {
        return Objects.hash(token, authenticationId, username, clientId, authentication, refreshToken)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        if (other == null || this.javaClass.kotlin !== other.javaClass) {
            return false
        }

        val obj = other as AccessToken

        return (this.token.contentEquals(obj.token)
                && this.authenticationId == obj.authenticationId
                && this.username == obj.username
                && this.clientId == obj.clientId
                && this.authentication.contentEquals(obj.authentication)
                && this.refreshToken == obj.refreshToken)
    }

    companion object {

        const val COLLECTION_NAME = "access_tokens"
    }
}
