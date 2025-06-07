package uni.projects.remarketbackend.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uni.projects.remarketbackend.dto.ListingDto;
import uni.projects.remarketbackend.dto.order.OrderDto;
import uni.projects.remarketbackend.models.listing.ListingStatus;
import uni.projects.remarketbackend.models.order.OrderStatus;
import uni.projects.remarketbackend.services.SellerService;

import java.util.List;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 07.06.2025
 */

@RestController
@RequestMapping("/api/seller")
public class SellerController {

    @Autowired
    private SellerService sellerService;

    @GetMapping("/orders")
    @Transactional
    public ResponseEntity<List<OrderDto>> getItems(HttpServletRequest request) {
        List<OrderDto> orders = sellerService.getOrdersForSeller(request);
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/orders/{listingOrderId}/status")
    @Transactional
    public ResponseEntity<Void> updateListingOrderStatus(@PathVariable Long listingOrderId, HttpServletRequest request) {
        sellerService.setListingOrderStatusShipped(listingOrderId, request);
        return ResponseEntity.noContent().build();
    }
}
