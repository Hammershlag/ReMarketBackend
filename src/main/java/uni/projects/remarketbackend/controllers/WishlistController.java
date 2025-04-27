package uni.projects.remarketbackend.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uni.projects.remarketbackend.dto.WishlistDto;
import uni.projects.remarketbackend.models.Wishlist;
import uni.projects.remarketbackend.services.WishlistService;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 27.04.2025
 */

@RestController
@RequestMapping("/api/wishlists")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @GetMapping
    @Transactional
    public ResponseEntity<WishlistDto> getWishlist(HttpServletRequest request) {
        return ResponseEntity.ok(wishlistService.getWishlist(request));
    }

}
