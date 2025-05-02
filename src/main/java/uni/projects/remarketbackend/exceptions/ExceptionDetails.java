package uni.projects.remarketbackend.exceptions;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "Timestamp of the exception", example = "2025-04-25T12:34:56")
    private final LocalDateTime timestamp;

    @Schema(description = "HTTP status of the exception", example = "404")
    private final HttpStatus status;

    @Schema(description = "Message describing the exception", example = "Account not found")
    private final String errorMessage;

    @Schema(description = "Path where the exception occurred", example = "/api/accounts/1")
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
