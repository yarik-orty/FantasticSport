package io.makefun.fantasticsport.base

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version

abstract class Entity : Storable {

    @CreatedDate
    override var creationTime: Long? = null

    @LastModifiedDate
    var modificationTime: Long? = null

    @Version
    override var version: Long? = 0L

    object EntityMeta {
        const val ID = "_id"
        const val CREATION_TIME = "creationTime"
        const val MODIFICATION_TIME = "modificationTime"
    }
}
