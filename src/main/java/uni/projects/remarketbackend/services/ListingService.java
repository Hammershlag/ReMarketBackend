package uni.projects.remarketbackend.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uni.projects.remarketbackend.dao.*;
import uni.projects.remarketbackend.dto.ListingDto;
import uni.projects.remarketbackend.dto.PhotoDto;
import uni.projects.remarketbackend.dto.ReviewDto;
import uni.projects.remarketbackend.exceptions.exceptions.*;
import uni.projects.remarketbackend.models.*;
import uni.projects.remarketbackend.models.account.Account;
import uni.projects.remarketbackend.models.listing.Listing;
import uni.projects.remarketbackend.models.listing.ListingStatus;
import uni.projects.remarketbackend.models.review.Review;
import uni.projects.remarketbackend.models.review.ReviewStatus;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 27.04.2025
 */

@Service
public class ListingService {

    @Autowired
    private ListingRepository listingRepository;
    @Autowired
    private PhotoRepository photoRepository;
    @Autowired
    private AccountService accountService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private WishlistRepository wishlistRepository;
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;
    @Autowired
    private ReviewRepository reviewRepository;

    public Page<ListingDto> getListings(Optional<Double> minPrice, Optional<Double> maxPrice, Optional<Integer> categoryId,
                                        Optional<String> title, Optional<String> sort, int page, int pageSize) {
        Specification<Listing> spec = Specification.where((root, query, criteriaBuilder) ->
                criteriaBuilder.notEqual(root.get("status"), ListingStatus.BLOCKED));

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

    @SneakyThrows
    @Transactional
    public ListingDto createListing(HttpServletRequest request, ListingDto listingDto) {
        Account account = accountService.getAccount(request);
        if (account == null) {
            throw new AuthenticationException("User is not authenticated.");
        }

        if (StringUtils.isBlank(listingDto.getTitle())) {
            throw new ClientException("Title cannot be empty.");
        }

        if (listingDto.getPrice() <= 0) {
            throw new ClientException("Price must be greater than zero.");
        }

        if (StringUtils.isBlank(listingDto.getDescription()))
            throw new ClientException("Description cannot be empty.");

        Listing listing = new Listing();
        listing.setTitle(listingDto.getTitle());
        listing.setDescription(listingDto.getDescription());
        listing.setPrice(listingDto.getPrice());
        listing.setAverageRating(-1f);

        List<Photo> photos = photoRepository.findAllById(
                listingDto.getPhotos().stream()
                        .map(PhotoDto::getId)
                        .collect(Collectors.toList())
        );

        if (photos.isEmpty() && listingDto.getPhotos().size() > 0) {
            throw new ClientException("No valid photos provided.");
        }

        listing.setPhotos(photos);
        listing.setSeller(account);
        listing.setStatus(ListingStatus.ACTIVE);

        Category category = categoryService.getById(listingDto.getCategory().getId());
        if (category == null) {
            throw new NotFoundException("Category not found.");
        }

        listing.setCategory(category);
        Listing savedListing = listingRepository.save(listing);
        return ListingDto.valueFrom(savedListing);
    }

    @SneakyThrows
    public ListingDto updateListing(HttpServletRequest request, Long id, ListingDto listing) {
        Account account = accountService.getAccount(request);
        if (account == null) {
            throw new AuthenticationException("User is not authenticated.");
        }

        if (StringUtils.isBlank(listing.getTitle())) {
            throw new ClientException("Title cannot be empty.");
        }

        if (listing.getPrice() <= 0) {
            throw new ClientException("Price must be greater than zero.");
        }

        if (StringUtils.isBlank(listing.getDescription()))
            throw new ClientException("Description cannot be empty.");

        Listing existingListing = listingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Listing not found."));

        if (!existingListing.getSeller().getId().equals(account.getId())) {
            throw new AuthenticationException("You are not the owner of this listing.");
        }

        existingListing.setTitle(listing.getTitle());
        existingListing.setDescription(listing.getDescription());
        existingListing.setPrice(listing.getPrice());

        List<Photo> photos = photoRepository.findAllById(
                listing.getPhotos().stream()
                        .map(PhotoDto::getId)
                        .collect(Collectors.toList())
        );

        if (photos.isEmpty()) {
            throw new ClientException("No valid photos provided.");
        }

        existingListing.setPhotos(photos);

        Category category = categoryService.getById(listing.getCategory().getId());
        if (category == null) {
            throw new NotFoundException("Category not found.");
        }

        existingListing.setCategory(category);
        Listing savedListing = listingRepository.save(existingListing);
        return ListingDto.valueFrom(savedListing);
    }

    @SneakyThrows
    public void deleteListing(HttpServletRequest request, Long id) {
        Account account = accountService.getAccount(request);
        if (account == null) {
            throw new AuthenticationException("User is not authenticated.");
        }

        Listing existingListing = listingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Listing not found."));

        if (!existingListing.getSeller().getId().equals(account.getId())) {
            throw new AuthenticationException("You are not the owner of this listing.");
        }

        if (existingListing.getStatus() == ListingStatus.ARCHIVED) {
            throw new ClientException("Listing is already archived.");
        }

        existingListing.setStatus(ListingStatus.ARCHIVED);
        listingRepository.save(existingListing);
    }

    @SneakyThrows
    public ListingDto getListing(Long id) {
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Listing not found."));
        if (listing.getStatus() == ListingStatus.BLOCKED) {
            throw new ClientException("Listing has been blocked by admin.");
        }
        listing.setReviews(
                listing.getReviews().stream()
                        .filter(review -> review.getStatus() == ReviewStatus.ACTIVE)
                        .collect(Collectors.toSet())
        );
        return ListingDto.valueFrom(listing);
    }

    @SneakyThrows
    public void addToWishlist(HttpServletRequest request, Long id) {
        Account account = accountService.getAccount(request);
        if (account == null) {
            throw new AuthenticationException("User is not authenticated.");
        }

        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Listing not found."));

        if (account.getWishlist() == null) {
            Wishlist wishlist = new Wishlist();
            wishlist.setListings(new ArrayList<>());
            wishlistRepository.save(wishlist);
            account.setWishlist(wishlist);
            accountRepository.save(account);
        }

        if (account.getWishlist().getListings().contains(listing)) {
            throw new ClientException("Listing already in wishlist.");
        }

        account.getWishlist().getListings().add(listing);
        wishlistRepository.save(account.getWishlist());
        accountRepository.save(account);
    }

    @SneakyThrows
    public void removeFromWishlist(HttpServletRequest request, Long id) {
        Account account = accountService.getAccount(request);
        if (account == null) {
            throw new AuthenticationException("User is not authenticated.");
        }

        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Listing not found."));

        if (account.getWishlist() == null || !account.getWishlist().getListings().contains(listing)) {
            throw new NotFoundException("Listing not in wishlist.");
        }

        account.getWishlist().getListings().remove(listing);
        wishlistRepository.save(account.getWishlist());
        accountRepository.save(account);
    }

    @SneakyThrows
    public void addToShoppingCart(HttpServletRequest request, Long id) {
        Account account = accountService.getAccount(request);
        if (account == null) {
            throw new AuthenticationException("User is not authenticated.");
        }

        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Listing not found."));

        if (account.getShoppingCart() == null) {
            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setListings(new ArrayList<>());
            shoppingCartRepository.save(shoppingCart);
            account.setShoppingCart(shoppingCart);
            accountRepository.save(account);
        }

        if (account.getShoppingCart().getListings().contains(listing)) {
            throw new ClientException("Listing already in shopping cart.");
        }

        account.getShoppingCart().getListings().add(listing);
        shoppingCartRepository.save(account.getShoppingCart());
        accountRepository.save(account);
    }

    @SneakyThrows
    public void removeFromShoppingCart(HttpServletRequest request, Long id) {
        Account account = accountService.getAccount(request);
        if (account == null) {
            throw new AuthenticationException("User is not authenticated.");
        }

        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Listing not found."));

        if (account.getShoppingCart() == null || !account.getShoppingCart().getListings().contains(listing)) {
            throw new NotFoundException("Listing not in shopping cart.");
        }

        account.getShoppingCart().getListings().remove(listing);
        shoppingCartRepository.save(account.getShoppingCart());
        accountRepository.save(account);
    }

    @SneakyThrows
    public void addReview(HttpServletRequest request, Long id, ReviewDto reviewDto) {
        Account account = accountService.getAccount(request);
        if (account == null) {
            throw new AuthenticationException("User is not authenticated.");
        }

        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Listing not found."));

        if (listing.getReviews().stream().anyMatch(r -> r.getReviewer().getId().equals(account.getId()))) {
            throw new ClientException("You have already reviewed this listing.");
        }

        Review reviewEntity = new Review();
        reviewEntity.setRating(reviewDto.getRating());

        if (reviewEntity.getRating() < 1 || reviewEntity.getRating() > 5) {
            throw new ClientException("Rating must be between 1 and 5.");
        }
        if (StringUtils.isBlank(reviewDto.getTitle())) {
            throw new ClientException("Title cannot be empty.");
        }
        if (StringUtils.isBlank(reviewDto.getDescription())) {
            throw new ClientException("Description cannot be empty.");
        }
        reviewEntity.setTitle(reviewDto.getTitle());
        reviewEntity.setDescription(reviewDto.getDescription());
        reviewEntity.setReviewer(account);
        reviewEntity.setListing(listing);
        reviewEntity.setStatus(ReviewStatus.ACTIVE);
        reviewRepository.save(reviewEntity);

        listing.getReviews().add(reviewEntity);
        listing.setAverageRating((float) listing.getReviews().stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0));

        listingRepository.save(listing);
    }

    @SneakyThrows
    public Set<ReviewDto> getReviews(Long id) {
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Listing not found."));
        return listing.getReviews().stream()
                .map(ReviewDto::valueFrom).collect(Collectors.toSet());
    }

    @SneakyThrows
    public void deleteReview(HttpServletRequest request, Long id, Long reviewId) {
        Account account = accountService.getAccount(request);
        if (account == null) {
            throw new AuthenticationException("User is not authenticated.");
        }

        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Listing not found."));
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Review not found."));

        if (!review.getReviewer().getId().equals(account.getId())) {
            throw new AuthenticationException("You are not the owner of this review.");
        }

        listing.getReviews().remove(review);
        listing.setAverageRating((float) listing.getReviews().stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0));
        reviewRepository.delete(review);
        listingRepository.save(listing);
    }

    @SneakyThrows
    public void updateReview(HttpServletRequest request, Long id, Long reviewId, ReviewDto review) {
        Account account = accountService.getAccount(request);
        if (account == null) {
            throw new AuthenticationException("User is not authenticated.");
        }

        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Listing not found."));
        Review existingReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Review not found."));

        if (!existingReview.getReviewer().getId().equals(account.getId())) {
            throw new AuthenticationException("You are not the owner of this review.");
        }

        if (review.getRating() < 1 || review.getRating() > 5) {
            throw new ClientException("Rating must be between 1 and 5.");
        }
        if (StringUtils.isBlank(review.getTitle())) {
            throw new ClientException("Title cannot be empty.");
        }
        if (StringUtils.isBlank(review.getDescription())) {
            throw new ClientException("Description cannot be empty.");
        }

        existingReview.setRating(review.getRating());
        existingReview.setTitle(review.getTitle());
        existingReview.setDescription(review.getDescription());
        reviewRepository.save(existingReview);

        listing.setAverageRating((float) listing.getReviews().stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0));

        listingRepository.save(listing);
    }

    @SneakyThrows
    public void flagListing(HttpServletRequest request, Long id) {
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Listing not found."));
        if (listing.getStatus() == ListingStatus.UNDER_REVIEW) {
            throw new ClientException("Listing is already under review.");
        }
        if (listing.getStatus() == ListingStatus.BLOCKED) {
            throw new ClientException("Listing has already been blocked by admin.");
        }
        if (listing.getStatus() == ListingStatus.FLAGGED) {
            return;
        }
        Account account = accountService.getAccount(request);
        if (account == null) {
            throw new AuthenticationException("User is not authenticated.");
        }
        listing.setStatus(ListingStatus.FLAGGED);
        listingRepository.save(listing);
    }

    public void flagReview(HttpServletRequest request, Long id, Long reviewId) throws NotFoundException, ClientException, AuthenticationException {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Review not found."));
        if (review.getStatus() == ReviewStatus.UNDER_REVIEW) {
            throw new ClientException("Review is already under review.");
        }
        if (review.getStatus() == ReviewStatus.BLOCKED) {
            throw new ClientException("Review has already been blocked by admin.");
        }
        if (review.getStatus() == ReviewStatus.FLAGGED) {
            return;
        }
        Account account = accountService.getAccount(request);
        if (account == null) {
            throw new AuthenticationException("User is not authenticated.");
        }
        review.setStatus(ReviewStatus.FLAGGED);
        reviewRepository.save(review);
    }
}
