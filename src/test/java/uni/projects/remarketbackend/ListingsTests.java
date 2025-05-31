package uni.projects.remarketbackend;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import uni.projects.remarketbackend.dao.*;
import uni.projects.remarketbackend.models.account.*;
import uni.projects.remarketbackend.dto.*;
import uni.projects.remarketbackend.dto.auth.*;
import uni.projects.remarketbackend.services.*;
import  uni.projects.remarketbackend.services.auth.AuthService;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ListingsTests {

    @Autowired private MockMvc mockMvc;
    @Autowired private AccountRepository accountRepository;

    @Autowired private AccountService accountService;
    @Autowired private AuthService authService;
    @Autowired private ListingService listingService;
    @Autowired private CategoryService categoryService;
    @Autowired private ListingPhotoService listingPhotoService;

    private JwtAuthResponse tokens;
    private ListingDto listingDto;

    @BeforeAll
    void setUp() throws Exception {

        AccountDto accountDto = new AccountDto(
                "user123",
                "StrongPass123!",
                "johntest@example.com",
                Roles.ADMIN.getRole()
        );

        PhotoDto photoDto = new PhotoDto(
                1L,
                "testdata",
                "user123"
        );

        CategoryDto categoryDto = new CategoryDto(
                1L,
                "test"
        );

        accountService.createUser(accountDto);
        listingPhotoService.createPhoto(photoDto);
        categoryService.createCategory(categoryDto);

        listingDto = new ListingDto(
                1L,
                "Test Listing",
                "test desc",
                99.99,
                List.of(photoDto),
                accountDto.getUsername(),
                Status.ACTIVE.name(),
                categoryDto,
                null,
                4.5f
        );

        Account foundAccount = accountRepository.findByUsernameOrEmail(accountDto.getUsername(), accountDto.getEmail()).orElse(null);
        Assertions.assertNotNull(foundAccount);

        LoginDto loginDto = new LoginDto(accountDto.getUsername(), accountDto.getPassword());
        tokens = authService.login(loginDto);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + tokens.getAccessToken());

        listingService.createListing(request, listingDto);

    }

    @Test
    @Order(1)
    void testGetListings() throws Exception {
        mockMvc.perform(get("/api/listings")
                        .param("page", "1")
                        .param("pageSize", "10")
                        .header("Authorization", "Bearer " + tokens.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @Order(2)
    void testGetListingById() throws Exception {

        mockMvc.perform(get("/api/listings/1")
                        .header("Authorization", "Bearer " + tokens.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @Order(3)
    void testAddListingToWishlist() throws Exception {

        mockMvc.perform(post("/api/listings/" + listingDto.getId() + "/wishlist")
                        .header("Authorization", "Bearer " + tokens.getAccessToken()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/wishlists")
                        .header("Authorization", "Bearer " + tokens.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.listings[0].title").value("Test Listing"));
    }

    @Test
    @Order(4)
    void testRemoveFromWishlist() throws Exception {

        mockMvc.perform(delete("/api/listings/1/wishlist")
                        .header("Authorization", "Bearer " + tokens.getAccessToken()))
                .andExpect(status().isOk());
    }

    @Test
    @Order(5)
    void testAddToShoppingCart() throws Exception {
        mockMvc.perform(post("/api/listings/1/shopping-cart")
                        .header("Authorization", "Bearer " + tokens.getAccessToken()))
                .andExpect(status().isOk());
    }

    @Test
    @Order(6)
    void testRemoveFromShoppingCart() throws Exception {

        mockMvc.perform(delete("/api/listings/1/shopping-cart")
                        .header("Authorization", "Bearer " + tokens.getAccessToken()))
                .andExpect(status().isOk());
    }

    @Test
    @Order(7)
    void testAddReview() throws Exception {
        String reviewJson = """
                {
                    "rating": 5,
                    "title": "Great!",
                    "description": "Very satisfied"
                }
                """;

        mockMvc.perform(post("/api/listings/1/review")
                        .header("Authorization", "Bearer " + tokens.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reviewJson))
                .andExpect(status().isOk());
    }

    @Test
    @Order(8)
    void testGetReviews() throws Exception {

        mockMvc.perform(get("/api/listings/1/reviews")
                        .header("Authorization", "Bearer " + tokens.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Order(9)
    void testUpdateReview() throws Exception {
        String reviewJson = """
                {
                    "rating": 4,
                    "title": "Updated",
                    "description": "Updated description"
                }
                """;

        mockMvc.perform(put("/api/listings/1/review/1")
                        .header("Authorization", "Bearer " + tokens.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reviewJson))
                .andExpect(status().isOk());
    }

    @Test
    @Order(10)
    void testDeleteReview() throws Exception {
        mockMvc.perform(delete("/api/listings/1/review/1")
                        .header("Authorization", "Bearer " + tokens.getAccessToken()))
                .andExpect(status().isOk());
    }

}

