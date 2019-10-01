package io.makefun.fantasticsport.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST)
class BadRequestException(message: String? = null, throwable: Throwable? = null) : RuntimeException(message, throwable)