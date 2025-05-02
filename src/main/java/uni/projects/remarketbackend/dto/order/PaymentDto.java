package uni.projects.remarketbackend.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uni.projects.remarketbackend.models.order.payment.Currency;
import uni.projects.remarketbackend.models.order.payment.Payment;
import uni.projects.remarketbackend.models.order.payment.PaymentMethod;
import uni.projects.remarketbackend.models.order.payment.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 28.04.2025
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "PaymentDto", description = "Data transfer object for payment details")
public class PaymentDto {
    @Schema(description = "Unique identifier of the payment", example = "1")
    private Long id;

    @Schema(description = "Total amount of the payment", example = "99.99")
    private Double total;

    @Schema(description = "Payment method used for the payment", example = "CREDIT_CARD")
    private PaymentMethod paymentMethod;

    @Schema(description = "Status of the payment", example = "COMPLETED")
    private PaymentStatus paymentStatus;

    @Schema(description = "Currency used for the payment", example = "USD")
    private Currency currency;

    public static PaymentDto valueFrom(Payment payment) {
        return new PaymentDto(
                payment.getId(),
                payment.getTotal(),
                payment.getPaymentMethod(),
                payment.getPaymentStatus(),
                payment.getCurrency()
        );
    }

    public Payment convertTo() {
        Payment payment = new Payment();
        payment.setId(this.id);
        payment.setTotal(this.total);
        payment.setPaymentMethod(this.paymentMethod);
        payment.setPaymentStatus(this.paymentStatus);
        payment.setCurrency(this.currency);
        return payment;
    }
}
