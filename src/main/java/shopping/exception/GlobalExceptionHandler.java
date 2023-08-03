package shopping.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import shopping.exception.dto.ExceptionResponse;

// TODO: 정상 응답, 예외 응답 모두 통일된 규격으로
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ShoppingException.class)
    public ResponseEntity<ExceptionResponse> handleShoppingException(
        final ShoppingException exception
    ) {

        return ResponseEntity.badRequest().body(new ExceptionResponse(exception));
    }

    @ExceptionHandler(ShoppingAuthenticationException.class)
    public ResponseEntity<ExceptionResponse> handleShoppingAuthenticationException(
        final ShoppingAuthenticationException exception
    ) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ExceptionResponse(exception));
    }
}
