package pt.jorgeduarte.app.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pt.jorgeduarte.app.contracts.error.ApiErrorResponse;
import pt.jorgeduarte.app.exceptions.FileNotFoundException;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@ControllerAdvice
public class GlobalRestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({FileNotFoundException.class})
    public ResponseEntity<ApiErrorResponse.ApiErrorResponseBuilder> fileNotFound(FileNotFoundException ex, WebRequest request) {
        ApiErrorResponse.ApiErrorResponseBuilder apiResponse = ApiErrorResponse
                .ApiErrorResponseBuilder.builder()
                .message("We could not find the file you are looking for.")
                .detail("Please check the file name and try again.")
                .error_code("404")
                .status(HttpStatus.NOT_FOUND)
                .timeStamp(LocalDateTime.now(ZoneOffset.UTC)).build();
        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }
}