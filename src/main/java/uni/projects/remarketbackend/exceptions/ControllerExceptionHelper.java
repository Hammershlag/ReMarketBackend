package uni.projects.remarketbackend.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import uni.projects.remarketbackend.exceptions.exceptions.*;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 25.04.2025
 */
@ControllerAdvice
public class ControllerExceptionHelper {

    private static final Logger log = LoggerFactory.getLogger(ControllerExceptionHelper.class);

    /**
     * Use when the user is not authenticated
     * @param e
     * @return
     */
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public ResponseEntity<ExceptionDetails> handleAuthenticationException(AuthenticationException e) {
        //log.error("Authentication exception: " + e.getMessage());
        return ResponseEntity.status(406).body(new ExceptionDetails(HttpStatus.NOT_ACCEPTABLE, e.getMessage()));
    }

    /**
     * Use when the JWT token is not valid or expired
     * @param e
     * @return
     */
    @ExceptionHandler(JwtTokenException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ExceptionDetails> handleJwtTokenException(JwtTokenException e) {
        //log.error("JWT token exception: " + e.getMessage());
        return ResponseEntity.status(409).body(new ExceptionDetails(HttpStatus.CONFLICT, e.getMessage()));
    }


    /**
     * Use when the server is not able to process the request and other exceptions are not suitable
     * @param e
     * @return
     */
    @ExceptionHandler(CustomException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ExceptionDetails> handleCustomException(CustomException e) {
        //log.error("Custom exception: " + e.getMessage());
        return ResponseEntity.status(500).body(new ExceptionDetails(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()));
    }

    /**
     * Use when the data sent by the client is in wrong format or not valid
     * @param e
     * @return
     */
    @ExceptionHandler(ClientException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ExceptionDetails> handleClientException(ClientException e) {
        //log.error("Client exception: " + e.getMessage());
        return ResponseEntity.status(400).body(new ExceptionDetails(HttpStatus.BAD_REQUEST, e.getMessage()));
    }


    /**
     * Use when the resource is not found
     * @param e
     * @return
     */
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ExceptionDetails> handleNotFoundException(NotFoundException e) {
        //log.error("Not found exception: " + e.getMessage());
        return ResponseEntity.status(404).body(new ExceptionDetails(HttpStatus.NOT_FOUND, e.getMessage()));
    }

}