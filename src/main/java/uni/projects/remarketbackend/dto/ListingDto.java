package uni.projects.remarketbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uni.projects.remarketbackend.models.Category;
import uni.projects.remarketbackend.models.listing.Listing;
import uni.projects.remarketbackend.models.listing.ListingStatus;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 27.04.2025
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListingDto {

    private String title;
    private String description;;
    private double price;
    private List<PhotoDto> photos;
    private String sellerUsername;
    private String status;
    private Long categoryId;

    public static ListingDto valueFrom(Listing listing) {
        return new ListingDto(
                listing.getTitle(),
                listing.getDescription(),
                listing.getPrice(),
                listing.getPhotos().stream().map(PhotoDto::onlyId).collect(Collectors.toList()),
                listing.getSeller().getUsername(),
                listing.getStatus().name(),
                listing.getCategory().getId()
        );
    }

    public Listing convertTo() {
        Listing listing = new Listing();
        listing.setTitle(this.getTitle());
        listing.setDescription(this.getDescription());
        listing.setPrice(this.getPrice());
        listing.setPhotos(this.getPhotos().stream().map(PhotoDto::convertTo).collect(Collectors.toList()));
        listing.setStatus(ListingStatus.ACTIVE);
        Category category = new Category();
        category.setId(this.getCategoryId());
        listing.setCategory(category);
        // Seller should be set separately in the service layer
        return listing;
    }
}
