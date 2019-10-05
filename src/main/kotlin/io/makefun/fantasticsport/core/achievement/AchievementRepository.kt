package io.makefun.fantasticsport.core.achievement

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface AchievementRepository : MongoRepository<Achievement, String> {

    fun findByUserIdsContaining(userId: String): List<Achievement>
}