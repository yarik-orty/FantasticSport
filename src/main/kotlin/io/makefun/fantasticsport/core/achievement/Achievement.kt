package io.makefun.fantasticsport.core.achievement

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("achievements")
data class Achievement(
        @Id val id: String? = null,
        val userIds: MutableSet<String> = mutableSetOf(),
        val name: String,
        val type: AchievementType,
        val status: AchievementStatus)

enum class AchievementType {

}

enum class AchievementStatus {

}