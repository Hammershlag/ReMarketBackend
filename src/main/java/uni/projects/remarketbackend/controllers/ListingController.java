package uni.projects.remarketbackend.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uni.projects.remarketbackend.dto.ListingDto;
import uni.projects.remarketbackend.dto.ReviewDto;
import uni.projects.remarketbackend.exceptions.ExceptionDetails;
import uni.projects.remarketbackend.models.listing.Listing;
import uni.projects.remarketbackend.services.ListingService;

import java.util.Optional;
import java.util.Set;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 27.04.2025
 */

@Tag(name = "Listings", description = "Endpoints for managing listings")
@RestController
@RequestMapping("/api/listings")
public class ListingController {

    @Autowired
    private ListingService listingService;

    @Operation(summary = "Get all listings", description = "Retrieve a paginated list of all listings with optional filters.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved listings"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters", 
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", 
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class)))
    })
    @GetMapping
    @Transactional
    @SneakyThrows
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

    @Operation(summary = "Create a listing", description = "Create a new listing.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listing successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or missing fields", 
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class))),
            @ApiResponse(responseCode = "406", description = "Authentication required", 
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", 
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class)))
    })
    @PostMapping
    @Transactional
    @SneakyThrows
    public ResponseEntity<ListingDto> createListing(HttpServletRequest request, @RequestBody ListingDto listing) {
        ListingDto createdListing = listingService.createListing(request, listing);
        return createdListing != null ? ResponseEntity.ok(createdListing) : ResponseEntity.badRequest().build();
    }

    @Operation(summary = "Update a listing", description = "Update an existing listing.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listing successfully updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or missing fields", 
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class))),
            @ApiResponse(responseCode = "404", description = "Listing not found", 
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class))),
            @ApiResponse(responseCode = "406", description = "Authentication required", 
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", 
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class)))
    })
    @PutMapping("/{id}")
    @Transactional
    @SneakyThrows
    public ResponseEntity<ListingDto> updateListing(HttpServletRequest request, @PathVariable Long id, @RequestBody ListingDto listing) {
        return ResponseEntity.ok(listingService.updateListing(request, id, listing));
    }

    @Operation(summary = "Delete a listing", description = "Delete a listing by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Listing successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Listing not found", 
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class))),
            @ApiResponse(responseCode = "406", description = "Authentication required", 
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", 
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class)))
    })
    @DeleteMapping("/{id}")
    @Transactional
    @SneakyThrows
    public ResponseEntity<Void> deleteListing(HttpServletRequest request, @PathVariable Long id) {
        listingService.deleteListing(request, id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get a listing by ID", description = "Retrieve a listing by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listing successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Listing not found", 
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", 
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class)))
    })
    @GetMapping("/{id}")
    @Transactional
    @SneakyThrows
    public ResponseEntity<ListingDto> getListing(@PathVariable Long id) {
        return ResponseEntity.ok(listingService.getListing(id));
    }

    @Operation(summary = "Add a listing to wishlist", description = "Add a listing to the user's wishlist.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listing successfully added to wishlist"),
            @ApiResponse(responseCode = "404", description = "Listing not found", 
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class))),
            @ApiResponse(responseCode = "406", description = "Authentication required", 
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", 
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class)))
    })
    @PostMapping("/{id}/wishlist")
    @Transactional
    @SneakyThrows
    public ResponseEntity<Void> addToWishlist(HttpServletRequest request, @PathVariable Long id) {
        listingService.addToWishlist(request, id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Remove a listing from wishlist", description = "Remove a listing from the user's wishlist.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listing successfully removed from wishlist"),
            @ApiResponse(responseCode = "404", description = "Listing not found", 
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class))),
            @ApiResponse(responseCode = "406", description = "Authentication required", 
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", 
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class)))
    })
    @DeleteMapping("/{id}/wishlist")
    @Transactional
    @SneakyThrows
    public ResponseEntity<Void> removeFromWishlist(HttpServletRequest request, @PathVariable Long id) {
        listingService.removeFromWishlist(request, id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Add a listing to shopping cart", description = "Add a listing to the user's shopping cart.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listing successfully added to shopping cart"),
            @ApiResponse(responseCode = "404", description = "Listing not found", 
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class))),
            @ApiResponse(responseCode = "406", description = "Authentication required", 
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", 
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class)))
    })
    @PostMapping("/{id}/shopping-cart")
    @Transactional
    @SneakyThrows
    public ResponseEntity<Void> addToShoppingCart(HttpServletRequest request, @PathVariable Long id) {
        listingService.addToShoppingCart(request, id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Remove a listing from shopping cart", description = "Remove a listing from the user's shopping cart.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listing successfully removed from shopping cart"),
            @ApiResponse(responseCode = "404", description = "Listing not found", 
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class))),
            @ApiResponse(responseCode = "406", description = "Authentication required", 
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", 
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class)))
    })
    @DeleteMapping("/{id}/shopping-cart")
    @Transactional
    @SneakyThrows
    public ResponseEntity<Void> removeFromShoppingCart(HttpServletRequest request, @PathVariable Long id) {
        listingService.removeFromShoppingCart(request, id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get reviews for a listing", description = "Retrieve all reviews for a specific listing.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved reviews"),
            @ApiResponse(responseCode = "404", description = "Listing not found", 
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", 
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class)))
    })
    @GetMapping("/{id}/reviews")
    @Transactional
    @SneakyThrows
    public ResponseEntity<Set<ReviewDto>> getReviews(@PathVariable Long id) {
        return ResponseEntity.ok(listingService.getReviews(id));
    }

    @Operation(summary = "Add a review to a listing", description = "Add a review for a specific listing.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review successfully added"),
            @ApiResponse(responseCode = "400", description = "Invalid review data", 
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class))),
            @ApiResponse(responseCode = "404", description = "Listing not found", 
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class))),
            @ApiResponse(responseCode = "406", description = "Authentication required", 
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", 
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class)))
    })
    @PostMapping("/{id}/review")
    @Transactional
    @SneakyThrows
    public ResponseEntity<Void> addReview(HttpServletRequest request, @PathVariable Long id, @RequestBody ReviewDto review) {
        listingService.addReview(request, id, review);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Delete a review", description = "Delete a review for a specific listing.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Review or listing not found", 
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class))),
            @ApiResponse(responseCode = "406", description = "Authentication required", 
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", 
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class)))
    })
    @DeleteMapping("/{id}/review/{reviewId}")
    @Transactional
    @SneakyThrows
    public ResponseEntity<Void> deleteReview(HttpServletRequest request, @PathVariable Long id, @PathVariable Long reviewId) {
        listingService.deleteReview(request, id, reviewId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Update a review", description = "Update a review for a specific listing.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review successfully updated"),
            @ApiResponse(responseCode = "400", description = "Invalid review data", 
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class))),
            @ApiResponse(responseCode = "404", description = "Review or listing not found", 
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class))),
            @ApiResponse(responseCode = "406", description = "Authentication required", 
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", 
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class)))
    })
    @PutMapping("/{id}/review/{reviewId}")
    @Transactional
    @SneakyThrows
    public ResponseEntity<Void> updateReview(HttpServletRequest request, @PathVariable Long id, @PathVariable Long reviewId, @RequestBody ReviewDto review) {
        listingService.updateReview(request, id, reviewId, review);
        return ResponseEntity.ok().build();
    }
}
