package uni.projects.remarketbackend.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import uni.projects.remarketbackend.models.Account;

import java.util.Optional;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 25.04.2025
 */
public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    boolean existsByUsernameAndIdNot(String username, Long id);
    boolean existsByEmailAndIdNot(String email, Long id);

    Optional<Account> findByUsernameOrEmail(String username, String email);
}
