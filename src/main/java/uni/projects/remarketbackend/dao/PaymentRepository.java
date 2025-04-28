package uni.projects.remarketbackend.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import uni.projects.remarketbackend.models.order.payment.Payment;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 28.04.2025
 */
public interface PaymentRepository extends JpaRepository<Payment, Long> {

}
