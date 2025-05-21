package uni.projects.remarketbackend.services;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import uni.projects.remarketbackend.dao.ReviewRepository;
import uni.projects.remarketbackend.dto.ListingDto;
import uni.projects.remarketbackend.dto.ReviewDto;
import uni.projects.remarketbackend.exceptions.exceptions.ClientException;
import uni.projects.remarketbackend.models.listing.Listing;
import uni.projects.remarketbackend.models.listing.ListingStatus;
import uni.projects.remarketbackend.models.review.Review;
import uni.projects.remarketbackend.models.review.ReviewStatus;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 21.05.2025
 */

@Service
public class AdminReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    public Page<ReviewDto> getFlaggedReviews(int page, int pageSize) {
        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 10;

        return reviewRepository.findAllByStatus(ReviewStatus.UNDER_REVIEW, PageRequest.of(page - 1, pageSize))
                .map(ReviewDto::valueFrom);
    }

    @SneakyThrows
    public void blockReviews(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ClientException("Review not found"));
        review.setStatus(ReviewStatus.BLOCKED);
        reviewRepository.save(review);
    }

    @SneakyThrows
    public void dismissReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ClientException("Review not found"));
        review.setStatus(ReviewStatus.ACTIVE);
        reviewRepository.save(review);
    }

}
