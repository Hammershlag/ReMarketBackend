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
//        List<Order> orders = orderRepository.findOrdersBySellerId(sellerId);
//        return orders.stream()
//                .map(order -> {
//                    // Filter listings to include only those belonging to the seller
//                    List<Listing> filteredListings = order.getListings().stream()
//                            .filter(listing -> listing.getSeller().getId().equals(sellerId))
//                            .collect(Collectors.toList());
//                    order.setListings(filteredListings);
//                    return OrderDto.fromOrder(order);
//                })
//                .collect(Collectors.toList());
        return null;
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

    }
}
