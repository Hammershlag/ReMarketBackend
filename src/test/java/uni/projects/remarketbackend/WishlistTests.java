package uni.projects.remarketbackend;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import uni.projects.remarketbackend.dto.AccountDto;
import uni.projects.remarketbackend.dto.CategoryDto;
import uni.projects.remarketbackend.dto.ListingDto;
import uni.projects.remarketbackend.dto.PhotoDto;
import uni.projects.remarketbackend.dto.auth.JwtAuthResponse;
import uni.projects.remarketbackend.dto.auth.LoginDto;
import uni.projects.remarketbackend.models.account.Roles;
import uni.projects.remarketbackend.models.account.Status;
import uni.projects.remarketbackend.services.AccountService;
import uni.projects.remarketbackend.services.CategoryService;
import uni.projects.remarketbackend.services.ListingPhotoService;
import uni.projects.remarketbackend.services.ListingService;
import  uni.projects.remarketbackend.services.auth.AuthService;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
        import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WishlistTests {

    @Autowired private MockMvc mockMvc;

    @Autowired private CategoryService categoryService;
    @Autowired private AuthService authService;
    @Autowired private AccountService accountService;
    @Autowired private ListingService listingService;
    @Autowired private ListingPhotoService listingPhotoService;

    private  ListingDto listingDto;

    private JwtAuthResponse tokens;


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

        CategoryDto  categoryDto =  new CategoryDto(
                1L,
                "test"
        );

        var account = accountService.createUser(accountDto);
        account.setRole(Roles.ADMIN);
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



        LoginDto loginDto = new LoginDto(accountDto.getUsername(), accountDto.getPassword());
        tokens = authService.login(loginDto);


        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + tokens.getAccessToken());

        listingService.createListing(request, listingDto);

    }

    @Test
    void testGetWishlist() throws Exception {
        mockMvc.perform(post("/api/listings/" + listingDto.getId() + "/wishlist")
                        .header("Authorization", "Bearer " + tokens.getAccessToken()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/wishlists")
                        .header("Authorization", "Bearer " + tokens.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.listings").isArray());    }}