package uni.projects.remarketbackend;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.transaction.annotation.Transactional;
import uni.projects.remarketbackend.dao.ReviewRepository;
import uni.projects.remarketbackend.dto.*;
import uni.projects.remarketbackend.dto.auth.JwtAuthResponse;
import uni.projects.remarketbackend.dto.auth.LoginDto;
import uni.projects.remarketbackend.exceptions.exceptions.ClientException;
import uni.projects.remarketbackend.models.account.Roles;
import uni.projects.remarketbackend.models.account.Status;
import uni.projects.remarketbackend.models.review.Review;
import uni.projects.remarketbackend.models.review.ReviewStatus;
import uni.projects.remarketbackend.services.*;
import uni.projects.remarketbackend.services.auth.AuthService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AdminReviewTests {

    @Autowired private AdminReviewService adminReviewService;
    @Autowired private ReviewRepository reviewRepository;
    @Autowired private ListingService listingService;
    @Autowired private ListingPhotoService listingPhotoService;
    @Autowired private CategoryService categoryService;
    @Autowired private AccountService accountService;
    @Autowired private AuthService authService;
    @Autowired private StuffService stuffService;
    @Autowired private JdbcTemplate jdbcTemplate;

    private JwtAuthResponse tokens;

    private ReviewDto reviewDto;
    @BeforeAll
    void setup() throws Exception {
        AccountDto accountDto = new AccountDto("reviewAdmin", "Password123!", "reviewadmin@example.com", Roles.USER.getRole());
        var account = accountService.createUser(accountDto);
        account.setRole(Roles.ADMIN);

        LoginDto loginDto = new LoginDto(accountDto.getUsername(), accountDto.getPassword());
        tokens = authService.login(loginDto);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + tokens.getAccessToken());

        CategoryDto categoryDto = new CategoryDto(1L, "test");
        categoryService.createCategory(categoryDto);
        PhotoDto photoDto = new PhotoDto(1L, "testdata", accountDto.getUsername());
        listingPhotoService.createPhoto(photoDto);

        ListingDto listingDto = new ListingDto(
                1L, "Listing with Review", "desc", 50.0,
                List.of(photoDto), accountDto.getUsername(), Status.ACTIVE.name(), categoryDto, null, 5.0f
        );
        listingService.createListing(request, listingDto);

        reviewDto = new ReviewDto(1l, 2, "Suspicious review", "Suspicious desc", account.getId(), account.getUsername(),  ReviewStatus.ACTIVE.name());
        listingService.addReview(request, listingDto.getId(), reviewDto);

        stuffService.flagReview(reviewDto.getId());
    }

    @AfterAll
    void cleanup() {
        reviewRepository.deleteAll();
        jdbcTemplate.execute("ALTER TABLE reviews ALTER COLUMN id RESTART WITH 1");
    }

    @Test
    @Transactional
    @Order(1)
    void testGetFlaggedReviews() {
        var page = adminReviewService.getFlaggedReviews(1, 10);
        assertThat(page).isNotNull();
        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getContent().get(0).getStatus()).isEqualTo(ReviewStatus.UNDER_REVIEW.name());
    }

    @Test
    @Order(2)
    void testDismissReview() {
        adminReviewService.dismissReview(reviewDto.getId());
        Review updated = reviewRepository.findById(reviewDto.getId()).orElse(null);
        assertThat(updated).isNotNull();
        assertThat(updated.getStatus()).isEqualTo(ReviewStatus.ACTIVE);
    }

    @Test
    @Order(3)
    void testBlockReview() {
        adminReviewService.blockReviews(reviewDto.getId());
        Review updated = reviewRepository.findById(reviewDto.getId()).orElse(null);
        assertThat(updated).isNotNull();
        assertThat(updated.getStatus()).isEqualTo(ReviewStatus.BLOCKED);
    }
}
