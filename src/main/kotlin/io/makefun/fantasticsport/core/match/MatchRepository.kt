package io.makefun.fantasticsport.core.match

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface MatchRepository : MongoRepository<Match, String> {

    fun findByExternalId(externalId: String): Match?

    @Query("{ \$or : [ { 'homeTeam' : { \$in : ?0 } } , { 'awayTeam' : { \$in : ?0 } } ] }")
    fun findByTeamIds(teamIds: List<String>): List<Match>
}