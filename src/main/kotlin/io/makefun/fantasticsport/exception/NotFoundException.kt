package io.makefun.fantasticsport.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class NotFoundException(message: String? = null, throwable: Throwable? = null) : RuntimeException(message, throwable)