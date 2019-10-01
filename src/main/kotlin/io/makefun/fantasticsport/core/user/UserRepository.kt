package io.makefun.fantasticsport.core.user

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : MongoRepository<User, String> {

    fun findByName(name: String): User?

    fun findByEmail(email: String): User?

    fun findByEmailOrNumber(email: String, number: String?): User?

    @Query(value = "{ 'id' : { \$in : ?0 } }", fields = "{ 'lineup': ?1 }")
    fun findByIdIn(ids: Set<String>, withLineup: Int): List<User>

    @Query("{ 'team.name' : ?0 }")
    fun findByTeamName(name: String): User?
}