package pt.jorgeduarte.app.contracts.error;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ApiErrorResponse {

    //http status code
    private HttpStatus status;

    // in case we want to provide API based custom error code
    private String error_code;

    // customer error message to the client API
    private String message;

    // Any furthur details which can help client API
    private String detail;

    // Time of the error.make sure to define a standard time zone to avoid any confusion.
    private LocalDateTime timeStamp;

    // getter and setters
    //Builder
    @Getter
    @Setter
    @Builder
    public static final class ApiErrorResponseBuilder {
        private HttpStatus status;
        private String error_code;
        private String message;
        private String detail;
        private LocalDateTime timeStamp;

    }
}