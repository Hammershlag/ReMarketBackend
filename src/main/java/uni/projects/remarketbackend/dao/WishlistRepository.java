package uni.projects.remarketbackend.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import uni.projects.remarketbackend.models.Wishlist;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 27.04.2025
 */
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
}
