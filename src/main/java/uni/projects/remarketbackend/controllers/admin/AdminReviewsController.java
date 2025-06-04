package uni.projects.remarketbackend.controllers.admin;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uni.projects.remarketbackend.dto.ReviewDto;
import uni.projects.remarketbackend.services.AdminReviewService;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 21.05.2025
 */

@RestController
@RequestMapping("/api/admin/reviews")
public class AdminReviewsController {

    @Autowired
    private AdminReviewService adminReviewService;

    @GetMapping("/status/flagged")
    @Transactional
    public ResponseEntity<Page<ReviewDto>> getFlaggedReviews(@RequestParam(defaultValue = "1") int page,
                                                              @RequestParam(defaultValue = "10") int pageSize) {
        Page<ReviewDto> flaggedListings = adminReviewService.getFlaggedReviews(page, pageSize);
        return ResponseEntity.ok(flaggedListings);
    }

    @PutMapping("{id}/block")
    public ResponseEntity<Void> blockReview(@PathVariable Long id) {
        adminReviewService.blockReviews(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id}/dismiss")
    public ResponseEntity<Void> dismissReview(@PathVariable Long id) {
        adminReviewService.dismissReview(id);
        return ResponseEntity.noContent().build();
    }

}
