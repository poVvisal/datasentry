package dev.datasentry.shared.error;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Central exception handler. All handlers produce an {@link ApiError} JSON body so clients always
 * receive one consistent error shape regardless of which layer threw.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  // ---- Validation errors (400) -----------------------------------------------

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> handleValidation(
      MethodArgumentNotValidException ex, HttpServletRequest request) {

    List<ApiError.FieldError> fieldErrors =
        ex.getBindingResult().getFieldErrors().stream()
            .map(fe -> new ApiError.FieldError(fe.getField(), fe.getDefaultMessage()))
            .toList();

    String summary =
        fieldErrors.isEmpty()
            ? "Validation failed"
            : fieldErrors.get(0).field() + " " + fieldErrors.get(0).message();

    return build(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", summary, request, fieldErrors);
  }

  // ---- Type mismatch (400) e.g. UUID path variable not parseable ---------------

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ApiError> handleTypeMismatch(
      MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
    String msg =
        "Parameter '" + ex.getName() + "' has invalid value: '" + ex.getValue() + "'";
    return build(HttpStatus.BAD_REQUEST, "INVALID_PARAMETER", msg, request, null);
  }

  // ---- Not found (404) ---------------------------------------------------------

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ApiError> handleNotFound(
      EntityNotFoundException ex, HttpServletRequest request) {
    return build(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getMessage(), request, null);
  }

  // ---- Unhandled / internal server error (500) ---------------------------------

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> handleUnexpected(Exception ex, HttpServletRequest request) {
    log.error("Unhandled exception on {}: {}", request.getRequestURI(), ex.getMessage(), ex);
    return build(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "INTERNAL_ERROR",
        "An unexpected error occurred",
        request,
        null);
  }

  // ---- Helper ------------------------------------------------------------------

  private ResponseEntity<ApiError> build(
      HttpStatus status,
      String code,
      String message,
      HttpServletRequest request,
      List<ApiError.FieldError> fieldErrors) {

    String correlationId = MDC.get("correlationId");
    ApiError body =
        new ApiError(
            Instant.now(),
            status.value(),
            code,
            message,
            request.getRequestURI(),
            correlationId,
            fieldErrors);

    return ResponseEntity.status(status).body(body);
  }
}
