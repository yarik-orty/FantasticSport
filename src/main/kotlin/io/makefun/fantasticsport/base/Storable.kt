package io.makefun.fantasticsport.base

import com.fasterxml.jackson.annotation.JsonIgnore

import org.springframework.data.domain.Persistable

interface Storable : Persistable<String> {

    val creationTime: Long?

    val version: Long?

    @get:JsonIgnore
    val collectionName: String

    @JsonIgnore
    override fun isNew(): Boolean {
        return version == 0L && creationTime == null
    }
}
