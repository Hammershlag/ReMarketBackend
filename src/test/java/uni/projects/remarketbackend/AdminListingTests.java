package uni.projects.remarketbackend;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.transaction.annotation.Transactional;
import uni.projects.remarketbackend.dao.ListingRepository;
import uni.projects.remarketbackend.dto.AccountDto;
import uni.projects.remarketbackend.dto.CategoryDto;
import uni.projects.remarketbackend.dto.ListingDto;
import uni.projects.remarketbackend.dto.PhotoDto;
import uni.projects.remarketbackend.dto.auth.JwtAuthResponse;
import uni.projects.remarketbackend.dto.auth.LoginDto;
import uni.projects.remarketbackend.exceptions.exceptions.ClientException;
import uni.projects.remarketbackend.models.account.Roles;
import uni.projects.remarketbackend.models.account.Status;
import uni.projects.remarketbackend.models.listing.Listing;
import uni.projects.remarketbackend.models.listing.ListingStatus;
import uni.projects.remarketbackend.services.*;
import uni.projects.remarketbackend.services.auth.AuthService;
import uni.projects.remarketbackend.services.auth.AuthService.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AdminListingTests {

    @Autowired private AdminListingService adminListingService;
    @Autowired private ListingService listingService;
    @Autowired private ListingRepository listingRepository;
    @Autowired private AccountService accountService;
    @Autowired private ListingPhotoService listingPhotoService;
    @Autowired private CategoryService categoryService;
    @Autowired private AuthService authService;
    @Autowired private  StuffService stuffService;
    @Autowired private JdbcTemplate jdbcTemplate;

    private ListingDto listingDto;

    private JwtAuthResponse tokens;

    @BeforeAll
    void setup() throws Exception {
        AccountDto accountDto = new AccountDto("listingAdmin", "Password123!", "listingadmin@example.com", Roles.USER.getRole());
        var account = accountService.createUser(accountDto);
        account.setRole(Roles.ADMIN);

        CategoryDto categoryDto = new CategoryDto(1L, "test");
        categoryService.createCategory(categoryDto);

        PhotoDto photoDto = new PhotoDto(1L, "testdata", accountDto.getUsername());
        listingPhotoService.createPhoto(photoDto);

        listingDto = new ListingDto(
                1l,
                "Flagged Listing",
                "Listing for moderation tests",
                99.99,
                List.of(photoDto),
                accountDto.getUsername(),
                Status.ACTIVE.name(),
                categoryDto,
                null,
                4.0f
        );

        LoginDto loginDto = new LoginDto(accountDto.getUsername(), accountDto.getPassword());
        tokens = authService.login(loginDto);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + tokens.getAccessToken());

        listingService.createListing(request, listingDto);
        stuffService.flagListing(1l);
    }

    @AfterAll
    void cleanup() {
        listingRepository.deleteAll();
        jdbcTemplate.execute("ALTER TABLE listings ALTER COLUMN id RESTART WITH 1");
    }

    @Test
    @Transactional
    @Order(1)
    void testGetFlaggedListings() {
        Page<ListingDto> page = adminListingService.getFlaggedListings(1, 10);
        assertThat(page).isNotNull();
        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getContent().get(0).getStatus()).isEqualTo(ListingStatus.UNDER_REVIEW.name());
    }

    @Test
    @Order(2)
    void testDismissListing() {
        adminListingService.dismissListing(listingDto.getId());
        Listing updated = listingRepository.findById(listingDto.getId()).orElse(null);
        assertThat(updated).isNotNull();
        assertThat(updated.getStatus()).isEqualTo(ListingStatus.ACTIVE);
    }
}
