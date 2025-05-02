package uni.projects.remarketbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uni.projects.remarketbackend.models.ShoppingCart;
import uni.projects.remarketbackend.models.Wishlist;

import java.util.List;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 27.04.2025
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ShoppingCartDto", description = "Data transfer object for shopping cart details")
public class ShoppingCartDto {

    @Schema(description = "List of listings in the shopping cart")
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

