package uni.projects.remarketbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uni.projects.remarketbackend.models.Wishlist;
import uni.projects.remarketbackend.models.listing.Listing;

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
public class WishlistDto {

    private List<ListingDto> listings;

    public static WishlistDto valueFrom(Wishlist wishlist) {
        return new WishlistDto(
                wishlist.getListings().stream()
                        .map(ListingDto::valueFrom)
                        .collect(Collectors.toList())
        );
    }

    public Wishlist convertTo() {
        Wishlist wishlist = new Wishlist();
        wishlist.setListings(
                this.listings.stream()
                        .map(ListingDto::convertTo)
                        .collect(Collectors.toList())
        );
        return wishlist;
    }
}
