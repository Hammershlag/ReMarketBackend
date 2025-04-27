package uni.projects.remarketbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uni.projects.remarketbackend.models.ShoppingCart;
import uni.projects.remarketbackend.models.Wishlist;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 27.04.2025
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingCartDto {

    private List<ListingDto> listings;

    public static ShoppingCartDto valueFrom(ShoppingCart shoppingCart) {
        return new ShoppingCartDto(
                shoppingCart.getListings().stream()
                        .map(ListingDto::valueFrom)
                        .collect(Collectors.toList())
        );
    }

    public ShoppingCart convertTo() {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setListings(
                this.listings.stream()
                        .map(ListingDto::convertTo)
                        .collect(Collectors.toList())
        );
        return shoppingCart;
    }
}
