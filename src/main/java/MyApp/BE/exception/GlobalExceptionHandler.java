package MyApp.BE.exception;

import MyApp.BE.dto.ErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDTO> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        ErrorDTO errorDTO = new ErrorDTO("Neplatný parametr: " + ex.getMessage());
        return ResponseEntity.badRequest().body(errorDTO);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorDTO> handleRuntimeException(
            RuntimeException ex, WebRequest request) {
        ErrorDTO errorDTO = new ErrorDTO("Interní chyba serveru: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDTO);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> handleGeneralException(
            Exception ex, WebRequest request) {
        ErrorDTO errorDTO = new ErrorDTO("Neočekávaná chyba: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDTO);
    }
}