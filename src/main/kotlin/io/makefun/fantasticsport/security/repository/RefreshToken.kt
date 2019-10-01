package io.makefun.fantasticsport.security.repository

import io.makefun.fantasticsport.base.Entity
import org.springframework.data.annotation.Id

import org.springframework.data.annotation.PersistenceConstructor
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = RefreshToken.COLLECTION_NAME)
class RefreshToken @PersistenceConstructor constructor(
        @Id val _id: String,
        val token: ByteArray,
        val authentication: ByteArray) : Entity() {

    override fun getId(): String? {
        return _id
    }

    override val collectionName: String get() = COLLECTION_NAME

    companion object {

        const val COLLECTION_NAME = "refresh_tokens"
    }
}
