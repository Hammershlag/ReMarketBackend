package uni.projects.remarketbackend.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uni.projects.remarketbackend.models.account.Account;
import uni.projects.remarketbackend.models.account.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 25.04.2025
 */

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);


    boolean existsByUsernameAndIdNot(String username, Long id);
    boolean existsByEmailAndIdNot(String email, Long id);

    Optional<Account> findByUsernameOrEmail(String username, String email);

    long countByStatus(Status status);

    long countByDeletedAtIsNotNull();

    long countByCreatedAtAfter(LocalDateTime date);

    long countByLastLoginAfter(LocalDateTime date);
}
