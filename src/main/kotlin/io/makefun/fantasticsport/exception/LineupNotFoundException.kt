package io.makefun.fantasticsport.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Lineup not found")
class LineupNotFoundException(message: String? = null, throwable: Throwable? = null) : RuntimeException(message, throwable)