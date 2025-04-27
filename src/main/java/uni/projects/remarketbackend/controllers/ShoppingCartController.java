package uni.projects.remarketbackend.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uni.projects.remarketbackend.dto.ShoppingCartDto;
import uni.projects.remarketbackend.models.ShoppingCart;
import uni.projects.remarketbackend.services.ShoppingCartService;

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

    @GetMapping
    @Transactional
    public ResponseEntity<ShoppingCartDto> getShoppingCart(HttpServletRequest request) {
        return ResponseEntity.ok(shoppingCartService.getShoppingCart(request));
    }

}
