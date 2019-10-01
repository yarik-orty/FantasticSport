package io.makefun.fantasticsport.security.loggin


import io.makefun.fantasticsport.security.Security
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class LoggingService(private val repository: LoggingRepository) {

    fun login(userId: String): Logging {
        return repository.save(Logging.login(userId, Security.getIpAddress()))
    }

    fun logout(userId: String): Logging {
        return repository.save(Logging.logout(userId, Security.getIpAddress()))
    }

    fun findForUser(userId: String, pageable: Pageable): Page<Logging> {
        return repository.findByUserId(userId, pageable)
    }
}
