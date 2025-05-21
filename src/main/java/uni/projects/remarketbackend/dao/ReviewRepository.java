package uni.projects.remarketbackend.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uni.projects.remarketbackend.dto.ReviewDto;
import uni.projects.remarketbackend.models.review.Review;
import uni.projects.remarketbackend.models.review.ReviewStatus;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 28.04.2025
 */

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findAllByStatus(ReviewStatus status, Pageable pageable);
}
