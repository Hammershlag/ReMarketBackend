package uni.projects.remarketbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uni.projects.remarketbackend.models.review.Review;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 28.04.2025
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ReviewDto", description = "Data transfer object for review details")
public class ReviewDto {

    @Schema(description = "Unique identifier of the review", example = "1")
    private Long id;

    @Schema(description = "Rating given in the review", example = "5")
    private int rating;

    @Schema(description = "Title of the review", example = "Great product!")
    private String title;

    @Schema(description = "Description of the review", example = "The product exceeded my expectations.")
    private String description;

    @Schema(description = "ID of the listing being reviewed", example = "101")
    private Long listingId;

    @Schema(description = "Username of the reviewer", example = "reviewer123")
    private String reviewerUsername;

    public static ReviewDto valueFrom(Review review) {
        return new ReviewDto(
                review.getId(),
                review.getRating(),
                review.getTitle(),
                review.getDescription(),
                review.getListing() != null ? review.getListing().getId() : null,
                review.getReviewer() != null ? review.getReviewer().getUsername() : null
        );
    }

    public Review convertTo() {
        Review review = new Review();
        review.setId(this.id);
        review.setRating(this.rating);
        review.setTitle(this.title);
        review.setDescription(this.description);
        // Listing and Reviewer should be set in the service layer to avoid incomplete entities
        return review;
    }

}
