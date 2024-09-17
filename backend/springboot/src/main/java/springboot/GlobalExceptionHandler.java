package springboot;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TickerNotFoundException.class)
    public ResponseEntity<String> handleTickerNotFoundException(
            TickerNotFoundException ex) {

        return new ResponseEntity<>(ex.getMessage(),
                HttpStatus.NOT_FOUND);
    }
}