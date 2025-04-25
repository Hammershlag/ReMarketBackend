package uni.projects.remarketbackend.exceptions;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.http.HttpStatus;
import uni.projects.remarketbackend.utils.JsonDateDeserializer;
import uni.projects.remarketbackend.utils.JsonDateSerializer;

import java.time.LocalDateTime;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 25.04.2025
 */
public class ExceptionDetails {
    @JsonDeserialize(using = JsonDateDeserializer.class)
    @JsonSerialize(using = JsonDateSerializer.class)
    private final LocalDateTime timestamp;
    private final HttpStatus status;
    private final String errorMessage;
    private String path;

    public ExceptionDetails(HttpStatus status, String errorMessage) {
        timestamp = LocalDateTime.now();
        this.status = status;
        this.errorMessage = errorMessage;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
