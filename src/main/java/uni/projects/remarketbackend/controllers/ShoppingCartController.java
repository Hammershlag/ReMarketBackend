package uni.projects.remarketbackend.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uni.projects.remarketbackend.dto.ShoppingCartDto;
import uni.projects.remarketbackend.dto.order.OrderDto;
import uni.projects.remarketbackend.dto.order.OrderRequest;
import uni.projects.remarketbackend.models.ShoppingCart;
import uni.projects.remarketbackend.services.ShoppingCartService;
import uni.projects.remarketbackend.services.StripeService;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 27.04.2025
 */

@RestController
@RequestMapping("/api/shopping-carts")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @SneakyThrows
    @GetMapping
    @Transactional
    public ResponseEntity<ShoppingCartDto> getShoppingCart(HttpServletRequest request) {
        return ResponseEntity.ok(shoppingCartService.getShoppingCart(request));
    }

    @SneakyThrows
    @PostMapping("/checkout")
    @Transactional
    public ResponseEntity<Map<String, String>> checkout(HttpServletRequest request, @RequestBody OrderRequest orderRequest) {
        String sessionId = shoppingCartService.checkout(request, orderRequest);

        Map<String, String> responseData = new HashMap<>();
        responseData.put("id", sessionId); // Return the session ID

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(responseData);
    }

}
