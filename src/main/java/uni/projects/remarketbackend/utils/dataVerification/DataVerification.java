package uni.projects.remarketbackend.utils.dataVerification;

import uni.projects.remarketbackend.models.account.Account;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 27.04.2025
 */
public interface DataVerification {

    boolean verifyUser(Account account);
    boolean verifyUsername(String username);
    int passwordStrength(String password);
    boolean verifyEmail(String email);
    boolean verifyPassword(String password);
}
