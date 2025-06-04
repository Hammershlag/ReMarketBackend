package uni.projects.remarketbackend;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import uni.projects.remarketbackend.dao.AccountRepository;
import uni.projects.remarketbackend.dao.CategoryRepository;
import uni.projects.remarketbackend.dao.ListingRepository;
import uni.projects.remarketbackend.dao.PhotoRepository;
import uni.projects.remarketbackend.dto.AccountDto;
import uni.projects.remarketbackend.dto.CategoryDto;
import uni.projects.remarketbackend.dto.ListingDto;
import uni.projects.remarketbackend.dto.PhotoDto;
import uni.projects.remarketbackend.dto.auth.JwtAuthResponse;
import uni.projects.remarketbackend.dto.auth.LoginDto;
import uni.projects.remarketbackend.exceptions.exceptions.AuthenticationException;
import uni.projects.remarketbackend.exceptions.exceptions.ClientException;
import uni.projects.remarketbackend.exceptions.exceptions.NotFoundException;
import uni.projects.remarketbackend.models.Photo;
import uni.projects.remarketbackend.models.account.Account;
import uni.projects.remarketbackend.models.account.Roles;
import uni.projects.remarketbackend.models.account.Status;
import uni.projects.remarketbackend.models.listing.Listing;
import uni.projects.remarketbackend.services.AccountService;
import uni.projects.remarketbackend.services.CategoryService;
import uni.projects.remarketbackend.services.ListingPhotoService;
import uni.projects.remarketbackend.services.ListingService;
import uni.projects.remarketbackend.services.auth.AuthService;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ListingPhotoTests {

    @Autowired private ListingPhotoService listingPhotoService;
    @Autowired private PhotoRepository photoRepository;
    @Autowired private ListingRepository listingRepository;
    @Autowired private AccountRepository accountRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private AccountService accountService;
    @Autowired private ListingService listingService;
    @Autowired private CategoryService categoryService;
    @Autowired private AuthService authService;
    @Autowired private JdbcTemplate jdbcTemplate;

    private Account testAccount;
    private Account anotherAccount;
    private Listing testListing;
    private Photo testPhoto;
    private MockHttpServletRequest request;
    private JwtAuthResponse tokens;

    @BeforeAll
    void setup() throws Exception {
        AccountDto accountDto = new AccountDto("photouser", "Password123!", "photouser@example.com", Roles.USER.getRole());
        testAccount = accountService.createUser(accountDto);
        testAccount.setStatus(Status.ACTIVE);
        testAccount.setCreatedAt(LocalDateTime.now());
        testAccount.setUpdatedAt(LocalDateTime.now());
        accountRepository.save(testAccount);

        AccountDto anotherAccountDto = new AccountDto("anotheruser", "Password123!", "another@example.com", Roles.USER.getRole());
        anotherAccount = accountService.createUser(anotherAccountDto);
        anotherAccount.setStatus(Status.ACTIVE);
        anotherAccount.setCreatedAt(LocalDateTime.now());
        anotherAccount.setUpdatedAt(LocalDateTime.now());
        accountRepository.save(anotherAccount);

        LoginDto loginDto = new LoginDto(accountDto.getUsername(), accountDto.getPassword());
        tokens = authService.login(loginDto);
        request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + tokens.getAccessToken());

        CategoryDto categoryDto = new CategoryDto(1L, "Electronics");
        categoryService.createCategory(categoryDto);

        PhotoDto photoDto = new PhotoDto(null, Base64.getEncoder().encodeToString("test data".getBytes()), "photouser");
        testPhoto = listingPhotoService.createPhoto(photoDto);

        ListingDto listingDto = new ListingDto(
                null, "Test Listing", "Test description", 100.0,
                List.of(PhotoDto.valueFrom(testPhoto)), "photouser", Status.ACTIVE.name(), categoryDto, null, 5.0f
        );
        testListing = listingService.createListing(request, listingDto).convertTo();

        testListing.setSeller(testAccount);
        testListing = listingRepository.save(testListing);
    }

    @AfterAll
    void cleanup() {
        listingRepository.deleteAll();
        photoRepository.deleteAll();
        categoryRepository.deleteAll();
        accountRepository.deleteAll();

        jdbcTemplate.execute("ALTER TABLE listings ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE photos ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE categories ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE accounts ALTER COLUMN id RESTART WITH 1");
    }

    @Test
    @Transactional
    @Order(1)
    void testUploadPhotoSuccessfully() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "photo", "test.jpg", "image/jpeg", "test content".getBytes()
        );

        PhotoDto result = listingPhotoService.uploadPhoto(mockFile, testAccount);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getUploader()).isEqualTo("photouser");
    }
}