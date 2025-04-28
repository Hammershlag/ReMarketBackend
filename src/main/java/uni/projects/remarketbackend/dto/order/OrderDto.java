package uni.projects.remarketbackend.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uni.projects.remarketbackend.models.order.Order;
import uni.projects.remarketbackend.models.order.OrderStatus;
import uni.projects.remarketbackend.models.order.ShippingMethod;

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
public class OrderDto {
    private Long id;
    private AddressDto address;
    private Long buyerId;
    private PaymentDto payment;
    private List<Long> listingIds;
    private OrderStatus orderStatus;
    private LocalDateTime shippedDate;
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
