package uniandes.edu.co.proyecto.config;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ApiException.class)
  public ResponseEntity<ApiError> handleApi(ApiException ex, HttpServletRequest req) {
    var err = new ApiError(Instant.now(), ex.getStatus(), ex.getMessage(), req.getRequestURI());
    return ResponseEntity.status(ex.getStatus()).body(err);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ApiError> handleConstraint(DataIntegrityViolationException ex, HttpServletRequest req) {
    var err = new ApiError(Instant.now(), 409, "Violación de integridad de datos", req.getRequestURI());
    return ResponseEntity.status(409).body(err);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
    var msg = ex.getBindingResult().getFieldErrors().stream()
      .map(fe -> fe.getField() + ": " + fe.getDefaultMessage()).findFirst().orElse("Validación fallida");
    var err = new ApiError(Instant.now(), 400, msg, req.getRequestURI());
    return ResponseEntity.badRequest().body(err);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> handleOther(Exception ex, HttpServletRequest req) {
    var err = new ApiError(Instant.now(), 500, "Error interno: " + ex.getMessage(), req.getRequestURI());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
  }
}
