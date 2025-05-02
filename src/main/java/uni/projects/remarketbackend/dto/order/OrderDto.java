package uni.projects.remarketbackend.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uni.projects.remarketbackend.models.order.Order;
import uni.projects.remarketbackend.models.order.OrderStatus;
import uni.projects.remarketbackend.models.order.ShippingMethod;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 28.04.2025
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "OrderDto", description = "Data transfer object for order details")
public class OrderDto {
    @Schema(description = "Unique identifier of the order", example = "1")
    private Long id;

    @Schema(description = "Address details associated with the order")
    private AddressDto address;

    @Schema(description = "Unique identifier of the buyer", example = "42")
    private Long buyerId;

    @Schema(description = "Payment details associated with the order")
    private PaymentDto payment;

    @Schema(description = "List of listing IDs included in the order", example = "[101, 102, 103]")
    private List<Long> listingIds;

    @Schema(description = "Status of the order", example = "SHIPPING")
    private OrderStatus orderStatus;

    @Schema(description = "Date and time when the order was shipped", example = "2025-04-28T15:30:00")
    private LocalDateTime shippedDate;

    @Schema(description = "Shipping method used for the order", example = "STANDARD")
    private ShippingMethod shippingMethod;

    public static OrderDto valueFrom(Order order) {
        return new OrderDto(
                order.getId(),
                AddressDto.valueFrom(order.getAddress()),
                order.getBuyer().getId(),
                PaymentDto.valueFrom(order.getPayment()),
                order.getListings().stream().map(listing -> listing.getId()).toList(),
                order.getOrderStatus(),
                order.getShippedDate(),
                order.getShippingMethod()
        );
    }

    public Order convertTo() {
        Order order = new Order();
        order.setId(this.id);
        order.setAddress(this.address.convertTo());
        order.setPayment(this.payment.convertTo());
        order.setOrderStatus(this.orderStatus);
        order.setShippedDate(shippedDate);
        order.setShippingMethod(this.shippingMethod);
        return order;
    }
}

