package uni.projects.remarketbackend.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 07.02.2025
 */

@Data
@AllArgsConstructor
@Schema(name = "RefreshTokenRequest", description = "Data transfer object for refreshing the access token or verification token")
public class RefreshTokenRequest {

    @Schema(name = "refreshToken", description = "Refresh token for user to refresh the accessToken or verificationToken", example = "someRefreshToken")
    private String refreshToken;

    @Schema(name = "password", description = "User password, needed only when refreshing verificationToken", example = "SomePassword")
    private String password;

}
