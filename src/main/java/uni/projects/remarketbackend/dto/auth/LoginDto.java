package uni.projects.remarketbackend.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 07.02.2025
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "LoginDto", description = "Data transfer object for login")
public class LoginDto {

    @Schema(name = "usernameOrEmail", description = "Username or email of the user", example = "user123")
    private String usernameOrEmail;

    @Schema(name = "password", description = "Password of the user", example = "password123")
    private String password;
}