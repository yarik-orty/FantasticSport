package io.makefun.fantasticsport.core.match

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface TeamRepository : MongoRepository<Team, String> {

}