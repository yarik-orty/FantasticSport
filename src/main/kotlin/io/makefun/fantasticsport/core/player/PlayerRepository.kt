package io.makefun.fantasticsport.core.player

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface PlayerRepository : MongoRepository<Player, String> {

    fun findByIdIn(ids: List<String>): List<Player>
}