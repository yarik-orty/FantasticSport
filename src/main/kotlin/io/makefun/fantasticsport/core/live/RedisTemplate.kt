package io.makefun.fantasticsport.core.live

import org.springframework.stereotype.Component

@Component
class RedisTemplate {

    val data: MutableMap<String, RedisPlayer> = mutableMapOf()
}
