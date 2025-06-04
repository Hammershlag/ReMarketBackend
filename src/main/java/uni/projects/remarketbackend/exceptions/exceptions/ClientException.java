package uni.projects.remarketbackend.exceptions.exceptions;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 25.04.2025
 */
public class ClientException extends RuntimeException {
    public ClientException(String message) {
        super(message);
    }

    public ClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
