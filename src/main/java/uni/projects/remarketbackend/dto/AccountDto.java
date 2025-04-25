package uni.projects.remarketbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uni.projects.remarketbackend.models.Account;
import uni.projects.remarketbackend.models.Roles;

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

    public static AccountDto fromAccount(AccountDto account) {
        return new AccountDto(account.getUsername(), account.getPassword(), account.getEmail(), account.getRole());
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
