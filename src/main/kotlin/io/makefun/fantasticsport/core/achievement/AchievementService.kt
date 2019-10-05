package io.makefun.fantasticsport.core.achievement

import org.springframework.stereotype.Service

@Service
class AchievementService(private val repository: AchievementRepository) {

    fun find(userId: String): List<Achievement> {
        return repository.findByUserIdsContaining(userId)
    }

    fun create(achievement: Achievement) {
        repository.save(achievement)
    }
}