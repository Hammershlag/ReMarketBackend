package uni.projects.remarketbackend.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uni.projects.remarketbackend.models.Category;
import uni.projects.remarketbackend.models.account.Account;

import java.util.Optional;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 27.04.2025
 */

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByName(String name);
    Optional<Category> findByName(String name);

}
