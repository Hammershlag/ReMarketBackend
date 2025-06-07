package uni.projects.remarketbackend.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uni.projects.remarketbackend.dao.ListingOrderRepository;
import uni.projects.remarketbackend.dao.OrderRepository;
import uni.projects.remarketbackend.dao.ListingRepository;
import uni.projects.remarketbackend.dto.order.OrderDto;
import uni.projects.remarketbackend.models.account.Account;
import uni.projects.remarketbackend.models.listing.Listing;
import uni.projects.remarketbackend.models.listing.ListingStatus;
import uni.projects.remarketbackend.models.order.ListingOrder;
import uni.projects.remarketbackend.models.order.Order;
import uni.projects.remarketbackend.models.order.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 07.06.2025
 */

@Service
public class SellerService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ListingOrderRepository listingOrderRepository;

    @Autowired
    private AccountService accountService;

    public List<OrderDto> getOrdersForSeller(HttpServletRequest request) {
        Account account = accountService.getAccount(request);
        if (account == null) {
            throw new IllegalArgumentException("User not authenticated");
        }

        List<Order> orders = orderRepository.findOrdersContainingSellerItems(account.getId());

        return orders.stream()
                .map(order -> {
                    OrderDto orderDto = OrderDto.valueFrom(order);
                    orderDto.setListingOrders(orderDto.getListingOrders().stream()
                            .filter(listingOrderDto -> listingOrderDto.getListing().getSellerUsername().equals(account.getUsername()))
                            .collect(Collectors.toList()));
                    return orderDto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void setListingOrderStatusShipped(Long listingOrderId, HttpServletRequest request) {
        Account account = accountService.getAccount(request);
        ListingOrder listingOrder = listingOrderRepository.findListingOrderById(listingOrderId)
                .orElseThrow(() -> new IllegalArgumentException("ListingOrder not found"));

        if (!Objects.equals(listingOrder.getListing().getSeller().getId(), account.getId())) {
            throw new IllegalArgumentException("You are not the seller of this listing");
        }

        listingOrder.setListingStatus(OrderStatus.SHIPPED);
        listingOrderRepository.save(listingOrder);

        List<Order> orders = orderRepository.findAll()
                .stream()
                .filter(o -> o.getListings().contains(listingOrder))
                .toList();

        if (!orders.isEmpty()) {
            Order order = orders.getFirst();
            boolean allShipped = order.getListings().stream()
                    .allMatch(item -> OrderStatus.SHIPPED.equals(item.getListingStatus()));

            if (allShipped) {
                order.setOrderStatus(OrderStatus.SHIPPED);
                order.setShippedDate(LocalDateTime.now());
                orderRepository.save(order);
            }
        }
    }
}
