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
@Schema(name = "JwtAuthResponse", description = "Data transfer object for JWT authentication response")
public class JwtAuthResponse {

    public JwtAuthResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    @Schema(name = "accessToken", description = "Access token for user to use as authorization.\n" +
            "Returned as null when refreshing verificationToken", example = "someAccessToken")
    private String accessToken;

    @Schema(name = "refreshToken", description = "Refresh token for user to refresh the accessToken or verificationToken.\n" +
            "Returned as null when refreshing any type of token", example = "someRefreshToken")
    private String refreshToken;

    @Schema(name = "tokenType", description = "Type of the token, always set to Bearer", example = "Bearer")
    private String tokenType = "Bearer";
}
