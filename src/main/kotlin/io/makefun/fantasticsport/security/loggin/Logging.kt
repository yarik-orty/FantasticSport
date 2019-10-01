package io.makefun.fantasticsport.security.loggin

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import java.time.Instant

class Logging(
        @Id var id: String? = null,
        @Indexed(unique = true)
        var userId: String? = null,
        var type: Type? = null,
        var time: Instant? = null,
        var ip: String? = null) {

    enum class Type {
        LOGIN, LOGOUT
    }

    companion object {

        fun login(userId: String, ip: String): Logging {
            return createLogging(userId, ip, Type.LOGIN)
        }

        fun logout(userId: String, ip: String): Logging {
            return createLogging(userId, ip, Type.LOGOUT)
        }

        private fun createLogging(userId: String, ip: String, type: Type): Logging {
            val logging = Logging()
            logging.userId = userId
            logging.ip = ip
            logging.time = Instant.now()
            logging.type = type
            return logging
        }
    }
}
