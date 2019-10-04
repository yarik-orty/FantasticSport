package io.makefun.fantasticsport.core.game

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface GameRepository : MongoRepository<Game, String> {

    @Query("{ 'participants.userId' : ?0 }")
    fun findByUserId(userId: String): List<Game>

    fun findByIdIn(ids: List<String>): List<Game>

    fun findByRandomTrue(): List<Game>

    fun findByStatusAndTagsIn(status: GameStatus, matchId: String): List<Game>
}