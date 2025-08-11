package com.flavor.recipes.core

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class RestResponseEntityExceptionHandler
    : ResponseEntityExceptionHandler() {
    val log: Logger = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessError(e: BusinessException): ResponseEntity<Any> {
        return ResponseEntity<Any>(
            mapOf(
                "message" to e.message,
                "error_code" to e.codeError
            ),
            HttpStatus.UNPROCESSABLE_ENTITY
        )
    }

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFoundExceptionError(e: NotFoundException): ResponseEntity<Any> {
        return ResponseEntity<Any>(
            mapOf(
                "message" to e.message
            ),
            HttpStatus.NOT_FOUND
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleInternalServerError(e: Exception): ResponseEntity<Any> {
        log.error(e.toString(), e)
        return ResponseEntity<Any>(
            mapOf("message" to "Ocorreu um erro no serviço"),
            HttpStatus.INTERNAL_SERVER_ERROR
        )
    }
}