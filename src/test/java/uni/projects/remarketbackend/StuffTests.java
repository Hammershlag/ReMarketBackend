package uni.projects.remarketbackend;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import uni.projects.remarketbackend.dao.*;
import uni.projects.remarketbackend.dto.*;
import uni.projects.remarketbackend.dto.auth.*;
import uni.projects.remarketbackend.exceptions.exceptions.ClientException;
import uni.projects.remarketbackend.models.account.*;
import uni.projects.remarketbackend.models.listing.*;
import uni.projects.remarketbackend.models.review.*;
import uni.projects.remarketbackend.services.*;
import uni.projects.remarketbackend.services.auth.AuthService;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application-test.properties")
public class StuffTests {

    @Autowired private AccountRepository accountRepository;
    @Autowired private ListingRepository listingRepository;
    @Autowired private ReviewRepository reviewRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private PhotoRepository photoRepository;

    @Autowired private AccountService accountService;
    @Autowired private AuthService authService;
    @Autowired private ListingService listingService;
    @Autowired private CategoryService categoryService;
    @Autowired private ListingPhotoService listingPhotoService;
    @Autowired private StuffService stuffService;

    private JwtAuthResponse tokens;
    private Long listingId;
    private Long reviewId;


    @BeforeAll
    void setUp() throws Exception {
        reviewRepository.deleteAll();
        listingRepository.deleteAll();
        accountRepository.deleteAll();
        categoryRepository.deleteAll();
        photoRepository.deleteAll();

        AccountDto testAccount = new AccountDto(
                "testuser123",
                "StrongPass123!",
                "testuser@example.com",
                Roles.USER.getRole()
        );

        PhotoDto photoDto = new PhotoDto(
                1L,
                "testdata",
                "testuser123"
        );

        CategoryDto testCategory = new CategoryDto(
                1L,
                "Electronics"
        );

        accountService.createUser(testAccount);
        listingPhotoService.createPhoto(photoDto);
        categoryService.createCategory(testCategory);

        LoginDto loginDto = new LoginDto(testAccount.getUsername(), testAccount.getPassword());
        tokens = authService.login(loginDto);

        ListingDto testListing = new ListingDto(
                null,
                "iPhone 13",
                "Brand new iPhone 13 with 128GB storage",
                899.99,
                List.of(photoDto),
                testAccount.getUsername(),
                Status.ACTIVE.name(),
                testCategory,
                null,
                4.5f
        );

        org.springframework.mock.web.MockHttpServletRequest request =
                new org.springframework.mock.web.MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + tokens.getAccessToken());

        listingService.createListing(request, testListing);
        listingId = listingRepository.findAll().get(0).getId();

        Account account = accountRepository.findByUsernameOrEmail(testAccount.getUsername(), testAccount.getEmail()).orElse(null);
        Listing listing = listingRepository.findById(listingId).orElse(null);

        Review review = new Review();
        review.setRating(5);
        review.setTitle("Great product!");
        review.setDescription("Very satisfied with purchase");
        review.setReviewer(account);
        review.setListing(listing);
        review.setStatus(ReviewStatus.ACTIVE);

        Review savedReview = reviewRepository.save(review);
        reviewId = savedReview.getId();
    }

    @Test
    @Order(1)
    @Transactional(readOnly = true)
    void testGetListingsSuccess() {
        Page<ListingDto> result = stuffService.getListings(
                Optional.of(800.0),
                Optional.of(1000.0),
                Optional.of(1),
                Optional.of("iPhone"),
                Optional.of("price:desc"),
                1,
                10
        );

        assertNotNull(result);
        assertFalse(result.getContent().isEmpty());
        assertTrue(result.getContent().stream().allMatch(l -> l.getPrice() > 800.0 && l.getPrice() < 1000.0));
    }

    @Test
    @Order(2)
    @Transactional(readOnly = true)
    void testGetListingByIdSuccess() {
        ListingDto result = stuffService.getListingById(listingId);

        assertNotNull(result);
        assertEquals(listingId, result.getId());
        assertEquals("iPhone 13", result.getTitle());
    }

    @Test
    @Order(3)
    void testGetListingByIdNotFound() {
        Long nonExistentId = 99999L;

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            stuffService.getListingById(nonExistentId);
        });

        assertEquals("Listing not found", exception.getMessage());
    }

    @Test
    @Order(4)
    void testFlagListingSuccess() {
        assertDoesNotThrow(() -> {
            stuffService.flagListing(listingId);
        });

        Listing flaggedListing = listingRepository.findById(listingId).orElse(null);
        assertNotNull(flaggedListing);
        assertEquals(ListingStatus.UNDER_REVIEW, flaggedListing.getStatus());
    }

    @Test
    @Order(5)
    void testFlagListingNotFound() {
        Long nonExistentId = 99999L;

        ClientException exception = assertThrows(ClientException.class, () -> {
            stuffService.flagListing(nonExistentId);
        });

        assertEquals("Listing not found", exception.getMessage());
    }

    @Test
    @Order(6)
    void testFlagListingAlreadyBlocked() {
        Listing listing = listingRepository.findById(listingId).orElse(null);
        listing.setStatus(ListingStatus.BLOCKED);
        listingRepository.save(listing);

        ClientException exception = assertThrows(ClientException.class, () -> {
            stuffService.flagListing(listingId);
        });

        assertEquals("Listing is already blocked", exception.getMessage());
    }
}