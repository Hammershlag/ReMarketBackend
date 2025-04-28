package uni.projects.remarketbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uni.projects.remarketbackend.models.Review;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 28.04.2025
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDto {

    private Long id;
    private int rating;
    private String title;
    private String description;
    private Long listingId;
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
