package uni.projects.remarketbackend.dto.order;

import jakarta.annotation.Nonnull;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "OrderRequest", description = "Data transfer object for creating an order")
public class OrderRequest {

    @Schema(description = "Street name of the shipping address", example = "123 Main St")
    private String street;

    @Schema(description = "City of the shipping address", example = "New York")
    private String city;

    @Schema(description = "State of the shipping address", example = "NY")
    private String state;

    @Schema(description = "Zip code of the shipping address", example = "10001")
    private String zipCode;

    @Schema(description = "Country of the shipping address", example = "USA")
    private String country;

    @Schema(description = "Payment method for the order", example = "CREDIT_CARD")
    private PaymentMethod paymentMethod;

    @Schema(description = "Currency for the payment", example = "USD")
    private Currency currency;

    @Schema(description = "Shipping method for the order", example = "EXPRESS")
    private ShippingMethod shippingMethod;

}
