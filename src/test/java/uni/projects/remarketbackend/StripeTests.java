package uni.projects.remarketbackend;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.transaction.annotation.Transactional;
import uni.projects.remarketbackend.dao.*;
import uni.projects.remarketbackend.models.account.*;
import uni.projects.remarketbackend.models.listing.*;
import uni.projects.remarketbackend.models.order.Address;
import uni.projects.remarketbackend.models.order.OrderStatus;
import uni.projects.remarketbackend.models.order.ShippingMethod;
import uni.projects.remarketbackend.models.Category;
import uni.projects.remarketbackend.models.order.payment.*;
import uni.projects.remarketbackend.dto.*;
import uni.projects.remarketbackend.dto.auth.*;
import uni.projects.remarketbackend.services.*;
import uni.projects.remarketbackend.services.auth.AuthService;
import uni.projects.remarketbackend.services.StripeService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class StripeTests {

    @Autowired private AccountRepository accountRepository;
    @Autowired private ListingRepository listingRepository;
    @Autowired private CategoryRepository categoryRepository;

    @Autowired private AccountService accountService;
    @Autowired private AuthService authService;
    @Autowired private CategoryService categoryService;
    @Autowired private ListingPhotoService listingPhotoService;
    @Autowired private StripeService stripeService;

    private JwtAuthResponse tokens;
    private Account testAccount;
    private Listing testListing;

    @BeforeAll
    @Transactional
    void setUp() throws Exception {
        AccountDto accountDto = new AccountDto(
                "stripeuser123",
                "StrongPass123!",
                "stripetest@example.com",
                Roles.USER.getRole()
        );

        PhotoDto photoDto = new PhotoDto(
                1L,
                "stripeTestData",
                "stripeuser123"
        );

        CategoryDto categoryDto = new CategoryDto(
                1L,
                "Electronics"
        );

        accountService.createUser(accountDto);
        listingPhotoService.createPhoto(photoDto);
        categoryService.createCategory(categoryDto);

        testAccount = accountRepository.findByUsernameOrEmail(
                accountDto.getUsername(),
                accountDto.getEmail()
        ).orElse(null);
        assertNotNull(testAccount);

        LoginDto loginDto = new LoginDto(accountDto.getUsername(), accountDto.getPassword());
        tokens = authService.login(loginDto);

        Category category = categoryRepository.findById(1L).orElse(null);
        assertNotNull(category);

        testListing = new Listing();
        testListing.setTitle("Test Product");
        testListing.setDescription("Test product for Stripe payment");
        testListing.setPrice(99.99);
        testListing.setSeller(testAccount);
        testListing.setStatus(ListingStatus.ACTIVE);
        testListing.setCategory(category);


        testListing = listingRepository.save(testListing);
    }

    @Test
    @Order(1)
    void testCreateCheckoutSession() throws Exception {
        Address address = new Address();
        address.setStreet("123 Test Street");
        address.setCity("Test City");
        address.setZipCode("12345");
        address.setCountry("Test Country");

        Payment payment = new Payment();
        payment.setTotal(99.99);
        payment.setCurrency(Currency.USD);
        payment.setPaymentStatus(PaymentStatus.PENDING);

        uni.projects.remarketbackend.models.order.Order order = new uni.projects.remarketbackend.models.order.Order();
        order.setAddress(address);
        order.setBuyer(testAccount);
        order.setPayment(payment);
        order.setListings(List.of(testListing));
        order.setOrderStatus(OrderStatus.SHIPPING);
        order.setShippingMethod(ShippingMethod.STANDARD);

        String sessionId = stripeService.createCheckoutSession(order);

        assertNotNull(sessionId);
        assertFalse(sessionId.isEmpty());
    }
}