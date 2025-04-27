package uni.projects.remarketbackend.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uni.projects.remarketbackend.dto.ListingDto;
import uni.projects.remarketbackend.models.listing.Listing;
import uni.projects.remarketbackend.services.ListingService;

import java.util.Optional;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 27.04.2025
 */

@RestController
@RequestMapping("/api/listings")
public class ListingController {

    @Autowired
    private ListingService listingService;

    @GetMapping
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

        return ResponseEntity.ok(listingService.getListings(minPrice, maxPrice, categoryId, title, sort, page, pageSize));
    }

    @PostMapping
    @Transactional
    public ResponseEntity<ListingDto> createListing(HttpServletRequest request, @RequestBody ListingDto listing) {
        return ResponseEntity.ok(listingService.createListing(request, listing));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<ListingDto> updateListing(HttpServletRequest request, @PathVariable Long id, @RequestBody ListingDto listing) {
        return ResponseEntity.ok(listingService.updateListing(request, id, listing));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deleteListing(HttpServletRequest request, @PathVariable Long id) {
        listingService.deleteListing(request, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Transactional
    public ResponseEntity<ListingDto> getListing(@PathVariable Long id) {
        return ResponseEntity.ok(listingService.getListing(id));
    }

}
