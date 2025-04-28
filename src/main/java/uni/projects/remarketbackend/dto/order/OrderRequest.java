package uni.projects.remarketbackend.dto.order;

import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uni.projects.remarketbackend.models.order.ShippingMethod;
import uni.projects.remarketbackend.models.order.payment.Currency;
import uni.projects.remarketbackend.models.order.payment.PaymentMethod;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 28.04.2025
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {

    //Address
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String country;

    //Payment
    private PaymentMethod paymentMethod;
    private Currency currency;

    //Order
    private ShippingMethod shippingMethod;

}
