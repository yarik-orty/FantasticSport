package io.makefun.fantasticsport.core.user

import io.makefun.fantasticsport.api.dto.UserRequest
import io.makefun.fantasticsport.core.lineup.Lineup
import io.makefun.fantasticsport.exception.BadRequestException
import io.makefun.fantasticsport.exception.NotFoundException
import io.makefun.fantasticsport.security.Security
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class UserService(private val repository: UserRepository) {

    private val log = LoggerFactory.getLogger(this.javaClass.name)

    fun signUp(user: User) {
        val existing = repository.findByEmail(user.email)
        if (existing != null) {
            throw BadRequestException("User with this email already exist")
        } else {
            repository.save(user)
        }
    }

    fun finishRegistration(userId: String, userRequest: UserRequest) {
        val user = findById(userId)
        user.name = userRequest.name
        user.team = userRequest.team
        repository.save(user)
    }

    fun findById(id: String): User {
        return repository.findByIdOrNull(id) ?: throw NotFoundException("User not found for id: $id")
    }

    fun findByIds(ids: Set<String>, withLineup: Boolean): List<User> {
        return repository.findByIdIn(ids, if (withLineup) 1 else 0)
    }

    fun findByEmail(email: String): User {
        return repository.findByEmail(email) ?: throw NotFoundException("User not found for email: $email")
    }

    fun findByUsername(email: String): User? {
        return repository.findByEmail(email) // email is username for now
    }

    fun findByName(name: String): User {
        return repository.findByName(name) ?: throw NotFoundException("User not found for name: $name")
    }

    fun findByTeamName(name: String): User {
        return repository.findByTeamName(name) ?: throw NotFoundException("User not found for team: $name")
    }

    fun currentUser(): User? {
        val email = Security.currentUserLogin()
        return findByUsername(email)
    }

    fun saveLineUp(lineup: Lineup) {
        val user = repository.findByIdOrNull(lineup.userId)
                ?: throw NotFoundException("User not found for id: ${lineup.userId}")
        user.lineup = lineup
        repository.save(user)
    }

    fun save(user: User) {
        repository.save(user)
    }

    fun saveAll(users: List<User>) {
        repository.saveAll(users)
    }
}