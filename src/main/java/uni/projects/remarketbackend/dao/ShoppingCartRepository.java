package uni.projects.remarketbackend.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uni.projects.remarketbackend.models.ShoppingCart;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 27.04.2025
 */

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
}
