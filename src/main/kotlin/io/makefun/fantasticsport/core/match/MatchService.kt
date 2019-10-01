package io.makefun.fantasticsport.core.match

import io.makefun.fantasticsport.exception.NotFoundException
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class MatchService(private val repository: MatchRepository) {

    private val log = LoggerFactory.getLogger(this.javaClass.name)

    fun findById(matchId: String): Match {
        return repository.findByIdOrNull(matchId)
                ?: throw NotFoundException("Match not found id: $matchId")
    }

    fun findByExternalId(externalId: String): Match {
        return repository.findByExternalId(externalId)
                ?: throw NotFoundException("Match not found externalId: $externalId")
    }

    fun findByTeamIds(teamIds: List<String>): List<Match> {
        return repository.findByTeamIds(teamIds)
    }

    fun save(matches: List<Match>) {
        repository.saveAll(matches)
    }
}