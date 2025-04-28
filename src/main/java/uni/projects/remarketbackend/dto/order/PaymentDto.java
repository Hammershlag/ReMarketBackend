package uni.projects.remarketbackend.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uni.projects.remarketbackend.models.order.payment.Payment;
import uni.projects.remarketbackend.models.order.payment.PaymentMethod;
import uni.projects.remarketbackend.models.order.payment.PaymentStatus;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 28.04.2025
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDto {
    private Long id;
    private Double total;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;

    public static PaymentDto valueFrom(Payment payment) {
        return new PaymentDto(
                payment.getId(),
                payment.getTotal(),
                payment.getPaymentMethod(),
                payment.getPaymentStatus()
        );
    }

    public Payment convertTo() {
        Payment payment = new Payment();
        payment.setId(this.id);
        payment.setTotal(this.total);
        payment.setPaymentMethod(this.paymentMethod);
        payment.setPaymentStatus(this.paymentStatus);
        return payment;
    }
}
