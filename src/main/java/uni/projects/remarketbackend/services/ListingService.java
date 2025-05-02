package uni.projects.remarketbackend.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
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
import uni.projects.remarketbackend.models.*;
import uni.projects.remarketbackend.models.account.Account;
import uni.projects.remarketbackend.models.listing.Listing;
import uni.projects.remarketbackend.models.listing.ListingStatus;

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

    @SneakyThrows
    @Transactional
    public ListingDto createListing(HttpServletRequest request, ListingDto listingDto) {
        Account account = accountService.getAccount(request);
        Listing listing = new Listing();
        listing.setTitle(listingDto.getTitle());
        listing.setDescription(listingDto.getDescription());
        listing.setPrice(listingDto.getPrice());
        listing.setAverageRating(-1f);

        List<Photo> photos = new ArrayList<>(
                photoRepository.findAllById(
                listingDto.getPhotos().stream()
                        .map(PhotoDto::getId)
                        .collect(Collectors.toList())
        ));

//        List<Photo> photos = new ArrayList<>();
//        for (PhotoDto photoDto : listingDto.getPhotos()) {
//            Long photoId = photoDto.getId();
//            Photo photo = photoRepository.findById(photoId)
//                    .orElseThrow(() -> new RuntimeException("Photo with ID " + photoId + " not found"));
//
//            System.out.println("Photo: " + photo.getId() + " " + photo.getUploader().getUsername());
//            photos.add(photo);
//        }

        listing.setPhotos(photos);

        listing.setSeller(account);
        listing.setStatus(ListingStatus.ACTIVE);
        Category category = categoryService.getById(listingDto.getCategory().getId());
        listing.setCategory(category);
        Listing savedListing = listingRepository.save(listing);
        return ListingDto.valueFrom(savedListing);
    }

    @SneakyThrows
    public ListingDto updateListing(HttpServletRequest request, Long id, ListingDto listing) {

        Account account = accountService.getAccount(request);
        Listing existingListing = listingRepository.findById(id).orElseThrow(() -> new RuntimeException("Listing not found"));
        if (!existingListing.getSeller().getId().equals(account.getId())) {
            throw new RuntimeException("You are not the owner of this listing");
        }
        existingListing.setTitle(listing.getTitle());
        existingListing.setDescription(listing.getDescription());
        existingListing.setPrice(listing.getPrice());
        existingListing.setPhotos(new ArrayList<>(photoRepository.findAllById(
                listing.getPhotos().stream()
                        .map(PhotoDto::getId)
                        .collect(Collectors.toList()))
        ));
        Category category = categoryService.getById(listing.getCategory().getId());
        existingListing.setCategory(category);
        Listing savedListing = listingRepository.save(existingListing);
        return ListingDto.valueFrom(savedListing);
    }

    public void deleteListing(HttpServletRequest request, Long id) {

        Account account = accountService.getAccount(request);
        Listing existingListing = listingRepository.findById(id).orElseThrow(() -> new RuntimeException("Listing not found"));
        if (!existingListing.getSeller().getId().equals(account.getId())) {
            throw new RuntimeException("You are not the owner of this listing");
        }
        existingListing.setStatus(ListingStatus.ARCHIVED);
        listingRepository.save(existingListing);
    }

    public ListingDto getListing(Long id) {
        return listingRepository.findById(id)
                .map(ListingDto::valueFrom)
                .orElseThrow(() -> new RuntimeException("Listing not found"));
    }

    public void addToWishlist(HttpServletRequest request, Long id) {
        Account account = accountService.getAccount(request);
        Listing listing = listingRepository.findById(id).orElseThrow(() -> new RuntimeException("Listing not found"));
        if (account.getWishlist() == null) {
            Wishlist wishlist = new Wishlist();
            wishlist.setListings(new ArrayList<>());
            wishlistRepository.save(wishlist);
            account.setWishlist(wishlist);
            accountRepository.save(account);
        }
        if (account.getWishlist().getListings().contains(listing)) {
            throw new RuntimeException("Listing already in wishlist");
        }
        account.getWishlist().getListings().add(listing);
        wishlistRepository.save(account.getWishlist());
        accountRepository.save(account);
    }

    public void removeFromWishlist(HttpServletRequest request, Long id) {
        Account account = accountService.getAccount(request);
        Listing listing = listingRepository.findById(id).orElseThrow(() -> new RuntimeException("Listing not found"));
        if (account.getWishlist() == null) {
            throw new RuntimeException("Wishlist not found");
        }
        if (!account.getWishlist().getListings().contains(listing)) {
            throw new RuntimeException("Listing not in wishlist");
        }
        account.getWishlist().getListings().remove(listing);
        wishlistRepository.save(account.getWishlist());
        accountRepository.save(account);
    }

    public void addToShoppingCart(HttpServletRequest request, Long id) {
        Account account = accountService.getAccount(request);
        Listing listing = listingRepository.findById(id).orElseThrow(() -> new RuntimeException("Listing not found"));
        if (account.getShoppingCart() == null) {
            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setListings(new ArrayList<>());
            shoppingCartRepository.save(shoppingCart);
            account.setShoppingCart(shoppingCart);
            accountRepository.save(account);
        }
        if (account.getShoppingCart().getListings().contains(listing)) {
            throw new RuntimeException("Listing already in wishlist");
        }
        account.getShoppingCart().getListings().add(listing);
        shoppingCartRepository.save(account.getShoppingCart());
        accountRepository.save(account);
    }

    public void removeFromShoppingCart(HttpServletRequest request, Long id) {
        Account account = accountService.getAccount(request);
        Listing listing = listingRepository.findById(id).orElseThrow(() -> new RuntimeException("Listing not found"));
        if (account.getShoppingCart() == null) {
            throw new RuntimeException("Wishlist not found");
        }
        if (!account.getShoppingCart().getListings().contains(listing)) {
            throw new RuntimeException("Listing not in wishlist");
        }
        account.getShoppingCart().getListings().remove(listing);
        shoppingCartRepository.save(account.getShoppingCart());
        accountRepository.save(account);
    }

    public void addReview(HttpServletRequest request, Long id, ReviewDto reviewDto) {
        Account account = accountService.getAccount(request);
        Listing listing = listingRepository.findById(id).orElseThrow(() -> new RuntimeException("Listing not found"));
        if (listing.getReviews().stream().anyMatch(r -> r.getReviewer().getId().equals(account.getId()))) {
            throw new RuntimeException("You have already reviewed this listing");
        }
        Review reviewEntity = new Review();
        reviewEntity.setRating(reviewDto.getRating());
        reviewEntity.setTitle(reviewDto.getTitle());
        reviewEntity.setDescription(reviewDto.getDescription());
        reviewEntity.setReviewer(account);
        reviewEntity.setListing(listing);
        reviewRepository.save(reviewEntity);

        listing.getReviews().add(reviewEntity);
        listing.setAverageRating((float) listing.getReviews().stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0));

        listingRepository.save(listing);
    }

    public Set<ReviewDto> getReviews(Long id) {

        Listing listing = listingRepository.findById(id).orElseThrow(() -> new RuntimeException("Listing not found"));
        return listing.getReviews().stream()
                .map(ReviewDto::valueFrom).collect(Collectors.toSet());

    }

    public void deleteReview(HttpServletRequest request, Long id, Long reviewId) {

        Account account = accountService.getAccount(request);
        Listing listing = listingRepository.findById(id).orElseThrow(() -> new RuntimeException("Listing not found"));
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new RuntimeException("Review not found"));
        if (!review.getReviewer().getId().equals(account.getId())) {
            throw new RuntimeException("You are not the owner of this review");
        }
        listing.getReviews().remove(review);
        listing.setAverageRating((float) listing.getReviews().stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0));
        reviewRepository.delete(review);
        listingRepository.save(listing);
    }

    public void updateReview(HttpServletRequest request, Long id, Long reviewId, ReviewDto review) {

        Account account = accountService.getAccount(request);
        Listing listing = listingRepository.findById(id).orElseThrow(() -> new RuntimeException("Listing not found"));
        Review existingReview = reviewRepository.findById(reviewId).orElseThrow(() -> new RuntimeException("Review not found"));
        if (!existingReview.getReviewer().getId().equals(account.getId())) {
            throw new RuntimeException("You are not the owner of this review");
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
}
