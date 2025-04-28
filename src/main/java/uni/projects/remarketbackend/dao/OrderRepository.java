package uni.projects.remarketbackend.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import uni.projects.remarketbackend.models.account.Account;
import uni.projects.remarketbackend.models.order.Order;

import java.util.List;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 28.04.2025
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByBuyer(Account buyer);
}
