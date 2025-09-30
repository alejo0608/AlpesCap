// src/main/java/uniandes/edu/co/proyecto/config/ApiExceptionHandler.java
package uniandes.edu.co.proyecto.config;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.support.SQLStateSQLExceptionTranslator;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLException;
import java.util.Map;

@ControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<?> handleDataIntegrity(DataIntegrityViolationException ex) {
    Throwable root = ex.getMostSpecificCause();
    String msg = (root != null) ? root.getMessage() : ex.getMessage();
    return ResponseEntity.status(409).body(Map.of("error", "Violación de integridad de datos", "detail", msg));
  }

  @ExceptionHandler(SQLException.class)
  public ResponseEntity<?> handleSql(SQLException ex) {
    var translator = new SQLStateSQLExceptionTranslator();
    var dataAccessEx = translator.translate("SQL error", null, ex);
    return ResponseEntity.status(400).body(Map.of("error", "Error SQL", "sqlState", ex.getSQLState(), "detail", dataAccessEx != null ? dataAccessEx.getMessage() : ex.getMessage()));
  }
}
