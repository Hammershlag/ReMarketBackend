package uni.projects.remarketbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uni.projects.remarketbackend.models.account.Account;
import uni.projects.remarketbackend.models.account.Roles;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 25.04.2025
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "AccountDto", description = "Data transfer object for account details")
public class AccountDto {

    @Schema(description = "Username of the account", example = "user123")
    private String username;

    @Schema(description = "Password of the account (hidden when retrieved)", example = "********")
    private String password;

    @Schema(description = "Email address of the account", example = "user@example.com")
    private String email;

    @Schema(description = "Role of the account", example = "USER")
    private String role;

    public static AccountDto fromAccount(Account account) {
        return new AccountDto(account.getUsername(), "********", account.getEmail(), account.getRole().name());
    }

    public Account toAccount() {
        Account account = new Account();
        account.setUsername(getUsername());
        account.setPassword(getPassword());
        account.setEmail(getEmail());
        account.setRole(Roles.USER);
        account.setCreatedAt(null);
        account.setUpdatedAt(null);
        account.setLastLogin(null);
        account.setStatus(null);
        account.setDeletedAt(null);
        return account;
    }

}
