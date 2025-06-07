package uni.projects.remarketbackend.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uni.projects.remarketbackend.models.order.ListingOrder;

import java.util.Optional;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 07.06.2025
 */

@Repository
public interface ListingOrderRepository extends JpaRepository<ListingOrder, Long> {

    Optional<ListingOrder> findListingOrderById(Long id);
}
