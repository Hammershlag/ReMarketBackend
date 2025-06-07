package uni.projects.remarketbackend.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uni.projects.remarketbackend.models.account.Account;
import uni.projects.remarketbackend.models.order.Order;

import java.util.List;
import java.util.Optional;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 28.04.2025
 */

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByBuyer(Account buyer);

    @Query("SELECT DISTINCT o FROM Order o JOIN o.listings lo WHERE lo.listing.seller.id = :sellerId")
    List<Order> findOrdersContainingSellerItems(@Param("sellerId") Long sellerId);
}
