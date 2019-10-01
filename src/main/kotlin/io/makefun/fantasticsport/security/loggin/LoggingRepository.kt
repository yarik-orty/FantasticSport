package io.makefun.fantasticsport.security.loggin

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository

interface LoggingRepository : MongoRepository<Logging, String> {

    fun findByUserId(userId: String, pageable: Pageable): Page<Logging>
}
