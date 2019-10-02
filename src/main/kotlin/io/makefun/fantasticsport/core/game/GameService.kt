package io.makefun.fantasticsport.core.game

import io.makefun.fantasticsport.api.dto.GameRequest
import io.makefun.fantasticsport.api.dto.GameTimelineResponse
import io.makefun.fantasticsport.core.lineup.Lineup
import io.makefun.fantasticsport.core.lineup.LineupPlayer
import io.makefun.fantasticsport.core.live.RedisTemplate
import io.makefun.fantasticsport.core.match.MatchService
import io.makefun.fantasticsport.core.user.User
import io.makefun.fantasticsport.core.user.UserService
import io.makefun.fantasticsport.exception.BadRequestException
import io.makefun.fantasticsport.exception.LineupNotFoundException
import io.makefun.fantasticsport.exception.NotFoundException
import io.makefun.fantasticsport.extension.sumByLong
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class GameService(private val repository: GameRepository,
                  private val redisTemplate: RedisTemplate,
                  private val userService: UserService,
                  private val matchService: MatchService) {

    private val log = LoggerFactory.getLogger(this.javaClass.name)

    fun create(userId: String, gameRequest: GameRequest) {
        val user = userService.findById(userId)
        val lineup = user.lineup ?: throw LineupNotFoundException()
        if (user.wallet.amount < gameRequest.stake) throw BadRequestException("Not enough coins to play")

        val teams = lineup.players.map { it.teamId }.distinct().toList()
        val matches = matchService.findByTeamIds(teams)

        val participants = processParticipants(user, gameRequest)
        val tags = mutableSetOf(*matches.map { it.id!! }.toTypedArray())
        val startDate = matches.first().date
        val game = Game(name = gameRequest.name ?: "Default",
                type = GameType.SINGLE, status = GameStatus.PENDING, random = gameRequest.random,
                stake = gameRequest.stake, startDate = startDate, participants = participants, tags = tags)

        log.info("About to save game: $game")
        val savedGame = repository.save(game)

        log.info("About to reserve coins for user: ${user.id}")
        user.wallet.reserveBucket[savedGame.id!!] = game.stake
        user.wallet.amount = user.wallet.amount - game.stake
        userService.save(user)
    }

    fun join(userId: String, gameId: String) {
        val user = userService.findById(userId)
        val lineup = user.lineup ?: throw LineupNotFoundException()
        val game = repository.findByIdOrNull(gameId) ?: throw NotFoundException("Game not found for id: $gameId")

        if (user.wallet.amount < game.stake) throw BadRequestException("Not enough coins to play")

        log.info("About to reserve coins for user: ${user.id}")
        user.wallet.reserveBucket[game.id!!] = game.stake
        user.wallet.amount = user.wallet.amount - game.stake
        userService.save(user)

        game.join(user)
        val teams = lineup.players.map { it.teamId }.distinct().toList()
        val matches = matchService.findByTeamIds(teams)
        val startDate = matches.first().date
        game.startDate = if (game.startDate?.isBefore(startDate) == true) game.startDate else startDate
        game.tags.addAll(matches.map { it.id!! })

        repository.save(game)
    }

    fun gameTimeline(userId: String, gameId: String): GameTimelineResponse {
        val game = repository.findByIdOrNull(gameId) ?: throw NotFoundException("Game not found for id: $gameId")
        val opponentId = game.participants.filter { it.accepted }.first { it.userId != userId }.userId
        val lineup = userService.findById(userId).lineup ?: throw LineupNotFoundException()
        val opponentLineup = userService.findById(opponentId).lineup ?: throw LineupNotFoundException()

        val players = lineup.players.mapNotNull { redisTemplate.data[it.id] }
        val opponentPlayers = opponentLineup.players.mapNotNull { redisTemplate.data[it.id] }

        return GameTimelineResponse(players.flatMap { it.events }, opponentPlayers.flatMap { it.events })
    }

    fun gameLineup(userId: String, gameId: String): Pair<Lineup, Lineup> {
        val game = repository.findByIdOrNull(gameId) ?: throw NotFoundException("Game not found for id: $gameId")
        val opponentId = game.participants.filter { it.accepted }.first { it.userId != userId }.userId
        val lineup = userService.findById(userId).lineup ?: throw LineupNotFoundException()
        val opponentLineup = userService.findById(opponentId).lineup ?: throw LineupNotFoundException()
        syncLive(lineup.players + opponentLineup.players)
        return Pair(lineup, opponentLineup)
    }

    fun start(matchId: String) {
        log.info("About to start match with id: $matchId")
        val acceptedGames = findByStatus(GameStatus.ACCEPTED, matchId)
        val notStarted = findByStatus(GameStatus.PENDING, matchId)
        rollbackReservation(notStarted)
        notStarted.forEach { it.status = GameStatus.NOT_STARTED }
        acceptedGames.forEach { startGame(it) }
        repository.saveAll(acceptedGames)
        repository.saveAll(notStarted)
    }

    fun finish(matchId: String) {
        log.info("About to finish match with id: $matchId")
        val liveGames = findByStatus(GameStatus.LIVE, matchId) // handle NOT_FINISHED games
        val userIds = liveGames.flatMap { it.participants }.map { it.userId }.toSet()
        val users = userService.findByIds(userIds, true)
        liveGames.forEach { finishGame(it, users) }
        repository.saveAll(liveGames)
    }

    fun findById(gameId: String): Game {
        return repository.findByIdOrNull(gameId) ?: throw NotFoundException("Game not found for id: $gameId")
    }

    fun findAvailable(userId: String): List<Game> {
        return emptyList()
    }

    fun findRandom(userId: String): List<Game> {
        val limit = 5
        val randomGames = repository.findByRandomTrue()
        return randomGames.subList(0, if (randomGames.size >= limit) limit else randomGames.size)
    }

    fun findByUserId(userId: String, status: GameStatus?): List<Game> {
        val games = repository.findByUserId(userId)
        val liveGames = games.filter { it.status == GameStatus.LIVE }
        if (liveGames.isNotEmpty()) {
            val userIds = liveGames.flatMap { it.participants }.map { it.userId }.toSet()
            val users = userService.findByIds(userIds, true)
            syncLive(users.flatMap { it.lineup!!.players })
            val myScore = users.find { it.id == userId }?.lineup?.score() ?: 0
            liveGames.flatMap { it.participants }.filter { it.userId == userId }.forEach { it.score = myScore }
            liveGames.flatMap { it.participants }.filter { it.userId != userId }.forEach { participant ->
                participant.score = users.find { it.id == participant.userId }?.lineup?.score() ?: 0
            }
        }
        return games
    }

    fun findByStatus(status: GameStatus, matchId: String): List<Game> {
        return repository.findByStatusAndTagsIn(status, matchId)
    }

    private fun startGame(game: Game) {
        game.start()
    }

    private fun finishGame(game: Game, users: List<User>) {
        game.finish()
        syncLive(users.flatMap { it.lineup!!.players })
        val participants = users.filter { user -> game.participants.map { it.userId }.contains(user.id) }
        participants.forEach { it.score += it.lineup?.score() ?: 0; it.lineup = null } // TODO: to history
        calculateReservation(game, participants)
        userService.saveAll(participants)
        // store players from redis to history
    }

    private fun rollbackReservation(games: List<Game>) {
        val userIds = games.flatMap { it.participants }.map { it.userId }.toSet()
        val users = userService.findByIds(userIds, false)
        games.forEach { game ->
            val participantIds = game.participants.map { it.userId }
            users.filter { participantIds.contains(it.id) }.forEach {
                it.wallet.amount += it.wallet.reserveBucket[game.id] ?: 0
                it.wallet.reserveBucket.remove(game.id)
            }
        }
        userService.saveAll(users)
    }

    private fun calculateReservation(game: Game, participants: List<User>) {
        val winner = participants.maxBy { it.lineup!!.score() }!!
        val loser = participants.find { it.id != winner.id }!!
        if (winner.lineup?.score() == loser.lineup?.score()) {
            winner.wallet.amount += winner.wallet.reserveBucket[game.id] ?: 0
            loser.wallet.amount += loser.wallet.reserveBucket[game.id] ?: 0
            winner.wallet.reserveBucket.remove(game.id)
            loser.wallet.reserveBucket.remove(game.id)
        } else {
            val winnerAmount = participants.sumByLong { it.wallet.reserveBucket[game.id] ?: 0 }
            loser.wallet.reserveBucket.remove(game.id)
            winner.wallet.reserveBucket.remove(game.id)
            winner.wallet.amount += winnerAmount
        }
    }

    private fun processParticipants(user: User, game: GameRequest): MutableList<Participant> {
        val participants = mutableListOf(Participant(user.id!!, user.name, accepted = true, owner = true))
        if (!game.random) participants.add(Participant(game.opponentId, game.opponentName, accepted = false, owner = false))
        return participants
    }

    private fun syncLive(players: List<LineupPlayer>) {
        players.forEach { it.score = redisTemplate.data[it.id]?.score ?: it.score }
    }
}