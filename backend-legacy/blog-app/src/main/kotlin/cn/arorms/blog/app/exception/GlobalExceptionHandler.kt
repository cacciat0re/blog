package cn.arorms.blog.app.exception

import cn.arorms.framework.common.exception.BaseExceptionHandler
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.server.ResponseStatusException

/**
 * Global exception handler for REST API
 * @author Sheng
 * @version 1.2.4 2026-10-12
 * @since 2026-07-22
 */
@RestControllerAdvice
class GlobalExceptionHandler : BaseExceptionHandler() {

    /**
     * Handle validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<String> {
        val errors = ex.bindingResult.fieldErrors.joinToString(", ") { fieldError ->
            "${fieldError.field}: ${fieldError.defaultMessage}"
        }
        return ResponseEntity.badRequest().body("Validation Failed: $errors")
    }

    /**
     * Handle type mismatch errors
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatchException(ex: MethodArgumentTypeMismatchException): ResponseEntity<String> {
        return ResponseEntity.badRequest()
            .body("Parameter '${ex.name}' should be of type ${ex.requiredType?.simpleName}")
    }

    /**
     * Handle explicit HTTP status errors, e.g. not found or state conflict
     */
    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatusException(ex: ResponseStatusException): ResponseEntity<String> {
        return ResponseEntity.status(ex.statusCode).body(ex.reason ?: ex.statusCode.toString())
    }
}