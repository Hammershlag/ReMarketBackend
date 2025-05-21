package uni.projects.remarketbackend.controllers;

import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uni.projects.remarketbackend.dto.ListingDto;
import uni.projects.remarketbackend.dto.ReviewDto;
import uni.projects.remarketbackend.services.StuffService;

import java.util.Optional;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 08.05.2025
 */

@RestController
@RequestMapping("/api/stuff")
public class StuffController {

    @Autowired
    private StuffService stuffService;

    @GetMapping("/listings")
    @Transactional
    public ResponseEntity<Page<ListingDto>> getListings(
            @RequestParam Optional<Double> minPrice,
            @RequestParam Optional<Double> maxPrice,
            @RequestParam Optional<Integer> categoryId,
            @RequestParam Optional<String> title,
            @RequestParam Optional<String> sort,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {

        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 10;

        return ResponseEntity.ok(stuffService.getListings(minPrice, maxPrice, categoryId, title, sort, page, pageSize));
    }

    @GetMapping("/listings/{id}")
    @Transactional
    public ResponseEntity<ListingDto> getListingById(@PathVariable Long id) {
        return ResponseEntity.ok(stuffService.getListingById(id));
    }

    @GetMapping("/listings/flagged")
    @Transactional
    public ResponseEntity<Page<ListingDto>> getFlaggedListings(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {

        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 10;

        return ResponseEntity.ok(stuffService.getFlaggedListings(page, pageSize));
    }

    @PutMapping("/listings/{id}/status/flag")
    public ResponseEntity<Void> flagListing(@PathVariable Long id) {
        stuffService.flagListing(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/listings/{id}/status/dismiss")
    public ResponseEntity<Void> dismissFlagListing(@PathVariable Long id) {
        stuffService.dismissFlagListing(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/reviews/flagged")
    @Transactional
    public ResponseEntity<Page<ReviewDto>> getFlaggedReviews(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {

        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 10;

        return ResponseEntity.ok(stuffService.getFlaggedReviews(page, pageSize));
    }

    @SneakyThrows
    @PutMapping("/review/{id}/status/flag")
    public ResponseEntity<Void> flagReview(@PathVariable Long id) {
        stuffService.flagReview(id);
        return ResponseEntity.ok().build();
    }

    @SneakyThrows
    @PutMapping("/review/{id}/status/dismiss")
    public ResponseEntity<Void> dismissFlagReview(@PathVariable Long id) {
        stuffService.dismissFlagReview(id);
        return ResponseEntity.ok().build();
    }

}
