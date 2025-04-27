package uni.projects.remarketbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uni.projects.remarketbackend.models.account.Account;
import uni.projects.remarketbackend.models.account.Roles;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 25.04.2025
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDto {

    private String username;
    private String password;
    private String email;
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
