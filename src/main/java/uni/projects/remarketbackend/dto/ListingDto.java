package uni.projects.remarketbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uni.projects.remarketbackend.models.Category;
import uni.projects.remarketbackend.models.listing.Listing;
import uni.projects.remarketbackend.models.listing.ListingStatus;
import io.swagger.v3.oas.annotations.media.Schema;

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
@Schema(name = "ListingDto", description = "Data transfer object for listing details")
public class ListingDto {

    @Schema(description = "Unique identifier of the listing", example = "1")
    private Long id;

    @Schema(description = "Title of the listing", example = "Smartphone for sale")
    private String title;

    @Schema(description = "Description of the listing", example = "Brand new smartphone with 128GB storage.")
    private String description;

    @Schema(description = "Price of the listing", example = "299.99")
    private double price;

    @Schema(description = "List of photos associated with the listing")
    private List<PhotoDto> photos;

    @Schema(description = "Username of the seller", example = "seller123")
    private String sellerUsername;

    @Schema(description = "Status of the listing", example = "ACTIVE")
    private String status;

    @Schema(description = "Category details of the listing")
    private CategoryDto category;

    @Schema(description = "Set of reviews for the listing")
    private Set<ReviewDto> reviews;

    @Schema(description = "Average rating of the listing", example = "4.5")
    private float averageRating;

    public static ListingDto valueFrom(Listing listing) {
        return new ListingDto(
                listing.getId(),
                listing.getTitle(),
                listing.getDescription(),
                listing.getPrice(),
                listing.getPhotos().stream().map(PhotoDto::onlyId).collect(Collectors.toList()),
                listing.getSeller().getUsername(),
                listing.getStatus().name(),
                CategoryDto.valueFrom(listing.getCategory()),
                listing.getReviews().stream().map(ReviewDto::valueFrom).collect(Collectors.toSet()),
                listing.getAverageRating()
        );
    }

    public Listing convertTo() {
        Listing listing = new Listing();
        listing.setId(this.getId());
        listing.setTitle(this.getTitle());
        listing.setDescription(this.getDescription());
        listing.setPrice(this.getPrice());
        listing.setPhotos(this.getPhotos().stream().map(PhotoDto::convertTo).collect(Collectors.toList()));
        listing.setStatus(ListingStatus.ACTIVE);
        listing.setCategory(this.category.convertTo());
        listing.setAverageRating(this.getAverageRating());
        return listing;
    }
}
