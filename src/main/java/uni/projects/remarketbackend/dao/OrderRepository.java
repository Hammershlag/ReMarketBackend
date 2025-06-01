package uni.projects.remarketbackend.dao;

import org.springframework.data.jpa.repository.JpaRepository;
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
}
