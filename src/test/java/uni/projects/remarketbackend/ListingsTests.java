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
import uni.projects.remarketbackend.models.listing.ListingStatus;
import uni.projects.remarketbackend.services.*;
import  uni.projects.remarketbackend.services.auth.AuthService;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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
    @Autowired private ListingRepository listingRepository;

    @Autowired private AccountService accountService;
    @Autowired private AuthService authService;
    @Autowired private ListingService listingService;
    @Autowired private CategoryService categoryService;
    @Autowired private ListingPhotoService listingPhotoService;


    private JwtAuthResponse tokens;
    private AccountDto accountDto;
    private  PhotoDto photoDto;
    private CategoryDto categoryDto;
    private ListingDto listingDto;

    @BeforeAll
    void setUp() throws Exception {

        accountDto = new AccountDto(
                "user123",
                "StrongPass123!",
                "johntest@example.com",
                Roles.USER.getRole()
        );

        photoDto = new PhotoDto(
                1L,
                "testdata",
                "user123"
        );

        categoryDto =  new CategoryDto(
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

    }

    @Test
    void shouldGetListings() throws Exception {
        mockMvc.perform(get("/api/listings")
                        .param("page", "1")
                        .param("pageSize", "10")
                        .header("Authorization", "Bearer " + tokens.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void shouldCreateListing() throws Exception {
        String listingJson = """
                {
                    "title": "Test Listing",
                    "description": "Description",
                    "price": 10.5,
                    "category": { "id": 1 },
                    "photos": [{ "id": 1 }]
                }
                """;

        mockMvc.perform(post("/api/listings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(listingJson)
                        .header("Authorization", "Bearer " + tokens.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Listing"));
    }

    @Test
    void shouldUpdateListing() throws Exception {
        // Assume ID 1 belongs to the authenticated user
        String updatedJson = """
                {
                    "title": "Updated Listing",
                    "description": "Updated Description",
                    "price": 20.0,
                    "category": { "id": 1 },
                    "photos": [{ "id": 1 }]
                }
                """;

        mockMvc.perform(put("/api/listings/1")
                        .header("Authorization", "Bearer " + tokens.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Listing"));
    }

    @Test
    void shouldDeleteListing() throws Exception {
        mockMvc.perform(delete("/api/listings/1")
                        .header("Authorization", "Bearer " + tokens.getAccessToken()))
                .andExpect(status().isNoContent());

        assertThat(listingRepository.findById(1L).get().getStatus()).isEqualTo(ListingStatus.ARCHIVED);
    }

    @Test
    @Order(1)
    void testAddListingToWishlist() throws Exception {


        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + tokens.getAccessToken());

        listingService.createListing(request, listingDto);

        mockMvc.perform(post("/api/listings/" + listingDto.getId() + "/wishlist")
                .header("Authorization", "Bearer " + tokens.getAccessToken()))
        .andExpect(status().isOk());

        mockMvc.perform(get("/api/wishlists")
                        .header("Authorization", "Bearer " + tokens.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.listings[0].title").value("Test Listing"));
    }
}

