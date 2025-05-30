package uni.projects.remarketbackend.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uni.projects.remarketbackend.models.account.Account;
import uni.projects.remarketbackend.models.account.Status;
import uni.projects.remarketbackend.models.account.Roles;

import java.time.LocalDateTime;

/**
 * DTO for admin containing extended account details.
 * 
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 07.05.2025
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExtendedAccountDto {

    private Long id;
    private String username;
    private String email;
    private Roles role;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLogin;
    private LocalDateTime deletedAt;
    private LocalDateTime disabledAt;

    public static ExtendedAccountDto fromAccount(Account account) {
        return new ExtendedAccountDto(
            account.getId(),
            account.getUsername(),
            account.getEmail(),
            account.getRole(),
            account.getStatus(),
            account.getCreatedAt(),
            account.getUpdatedAt(),
            account.getLastLogin(),
            account.getDeletedAt(),
            account.getDisabledAt()
        );
    }

    public Account toAccount() {
        Account account = new Account();
        account.setId(this.id);
        account.setUsername(this.username);
        account.setEmail(this.email);
        account.setRole(this.role);
        account.setStatus(this.status);
        account.setCreatedAt(this.createdAt);
        account.setUpdatedAt(this.updatedAt);
        account.setLastLogin(this.lastLogin);
        account.setDeletedAt(this.deletedAt);
        account.setDisabledAt(this.disabledAt);
        return account;
    }
}
