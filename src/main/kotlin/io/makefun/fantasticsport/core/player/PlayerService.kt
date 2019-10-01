package io.makefun.fantasticsport.core.player

import io.makefun.fantasticsport.exception.NotFoundException
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class PlayerService(private val repository: PlayerRepository) {

    private val log = LoggerFactory.getLogger(this.javaClass.name)

    fun findByIds(ids: List<String>): List<Player> {
        // TODO: if not found player for id ???
        return repository.findByIdIn(ids)
    }

    fun findById(id: String): Player {
        return repository.findByIdOrNull(id) ?: throw NotFoundException("Player with id: $id not found")
    }

    fun save(players: List<Player>) {
        repository.saveAll(players)
    }

    fun findAll(): List<Player> {
        return repository.findAll()
    }
}