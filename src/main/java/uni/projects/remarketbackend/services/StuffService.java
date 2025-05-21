package uni.projects.remarketbackend.services;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uni.projects.remarketbackend.dao.ListingRepository;
import uni.projects.remarketbackend.dao.ReviewRepository;
import uni.projects.remarketbackend.dto.ListingDto;
import uni.projects.remarketbackend.dto.ReviewDto;
import uni.projects.remarketbackend.exceptions.exceptions.ClientException;
import uni.projects.remarketbackend.models.listing.Listing;
import uni.projects.remarketbackend.models.listing.ListingStatus;
import uni.projects.remarketbackend.models.review.Review;
import uni.projects.remarketbackend.models.review.ReviewStatus;

import java.util.List;
import java.util.Optional;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 08.05.2025
 */

@Service
public class StuffService {

    @Autowired
    private ListingService listingService;

    @Autowired
    private ListingRepository listingRepository;
    @Autowired
    private ReviewRepository reviewRepository;

    public Page<ListingDto> getListings(Optional<Double> minPrice, Optional<Double> maxPrice, Optional<Integer> categoryId,
                                        Optional<String> title, Optional<String> sort, int page, int pageSize) {
        Specification<Listing> spec = Specification.where(null);


        if (minPrice.isPresent()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThan(root.get("price"), minPrice.get()));
        }

        if (maxPrice.isPresent()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThan(root.get("price"), maxPrice.get()));
        }

        if (categoryId.isPresent()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("categoryId"), categoryId.get()));
        }

        if (title.isPresent()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get("title"), "%" + title.get() + "%"));
        }

        Sort sorting = Sort.by("id"); // Default sorting
        if (sort.isPresent()) {
            String[] sortParts = sort.get().split(":");
            String sortField = sortParts[0].toLowerCase();
            boolean sortOrder = sortParts.length > 1 && sortParts[1].equalsIgnoreCase("desc");
            sorting = sortOrder ? Sort.by(sortField).descending() : Sort.by(sortField).ascending();
        }

        return listingRepository.findAll(spec, PageRequest.of(page - 1, pageSize, sorting)).map(ListingDto::valueFrom);
    }

    public ListingDto getListingById(Long id) {
        return listingRepository.findById(id)
                .map(ListingDto::valueFrom)
                .orElseThrow(() -> new RuntimeException("Listing not found"));
    }

    @SneakyThrows
    public void flagListing(Long id) {
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new ClientException("Listing not found"));
        if (listing.getStatus() == ListingStatus.BLOCKED)
            throw new ClientException("Listing is already blocked");
        listing.setStatus(ListingStatus.UNDER_REVIEW);
        listingRepository.save(listing);
    }

    public Page<ListingDto> getFlaggedListings(int page, int pageSize) {
        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 10;

        return listingRepository.findAllByStatus(ListingStatus.FLAGGED, PageRequest.of(page - 1, pageSize))
                .map(ListingDto::valueFrom);
    }

    @SneakyThrows
    public void dismissFlagListing(Long id) {
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new ClientException("Listing not found"));
        if (listing.getStatus() != ListingStatus.FLAGGED)
            throw new ClientException("Listing is not flagged");
        listing.setStatus(ListingStatus.ACTIVE);
        listingRepository.save(listing);
    }

    public Page<ReviewDto> getFlaggedReviews(int page, int pageSize) {
        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 10;

        return reviewRepository.findAllByStatus(ReviewStatus.FLAGGED, PageRequest.of(page - 1, pageSize))
                .map(ReviewDto::valueFrom);

    }

    public void dismissFlagReview(Long id) throws ClientException {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ClientException("Review not found"));
        if (review.getStatus() != ReviewStatus.FLAGGED)
            throw new ClientException("Review is not flagged");
        review.setStatus(ReviewStatus.ACTIVE);
        reviewRepository.save(review);
    }

    public void flagReview(Long id) throws ClientException {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ClientException("Review not found"));
        if (review.getStatus() == ReviewStatus.BLOCKED)
            throw new ClientException("Review is already blocked");
        review.setStatus(ReviewStatus.UNDER_REVIEW);
        reviewRepository.save(review);
    }
}
