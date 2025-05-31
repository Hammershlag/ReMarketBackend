package uni.projects.remarketbackend;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import uni.projects.remarketbackend.dao.AccountRepository;
import uni.projects.remarketbackend.dto.AccountDto;
import uni.projects.remarketbackend.dto.auth.JwtAuthResponse;
import uni.projects.remarketbackend.dto.auth.LoginDto;
import uni.projects.remarketbackend.exceptions.exceptions.AuthenticationException;
import uni.projects.remarketbackend.exceptions.exceptions.ClientException;
import uni.projects.remarketbackend.models.account.Account;
import uni.projects.remarketbackend.models.account.Roles;
import uni.projects.remarketbackend.models.account.Status;
import uni.projects.remarketbackend.services.AccountService;
import uni.projects.remarketbackend.services.auth.AuthService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AccountTests {

    @Autowired private MockMvc mockMvc;
    @Autowired private AccountService accountService;
    @Autowired private AuthService authService;
    @Autowired private AccountRepository accountRepository;
    @Autowired private JdbcTemplate jdbcTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private AccountDto accountDto;
    private JwtAuthResponse tokens;
    private Account createdAccount;

    @BeforeAll
    void setUp() throws Exception {
        accountDto = new AccountDto("testUser", "StrongPass123!", "testuser@example.com", Roles.USER.getRole());
        createdAccount = accountService.createUser(accountDto);
        LoginDto loginDto = new LoginDto(accountDto.getUsername(), accountDto.getPassword());
        tokens = authService.login(loginDto);
    }

    @AfterAll
    void cleanUp() {
        accountRepository.deleteAll();
        jdbcTemplate.execute("ALTER TABLE accounts ALTER COLUMN id RESTART WITH 1");
    }

    @Test
    @Order(1)
    void testCreateUser() {
        assertThat(createdAccount).isNotNull();
        assertThat(createdAccount.getUsername()).isEqualTo("testUser");
        assertThat(createdAccount.getStatus()).isEqualTo(Status.ACTIVE);
    }

    @Test
    @Order(2)
    void testGetCurrentAccount() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + tokens.getAccessToken());
        Account fetched = accountService.getAccount(request);
        assertThat(fetched).isNotNull();
        assertThat(fetched.getUsername()).isEqualTo("testUser");
    }

    @Test
    @Order(3)
    void testUpdateAccount() throws Exception {
        AccountDto updatedDto = new AccountDto("updatedUser", "StrongPass123!", "updated@example.com", Roles.USER.getRole());
        Account updated = accountService.updateAccount(createdAccount, updatedDto);
        assertThat(updated.getUsername()).isEqualTo("updatedUser");
        assertThat(updated.getEmail()).isEqualTo("updated@example.com");
    }

    @Test
    @Order(4)
    void testBecomeSeller() throws Exception {
        accountService.becomeSeller(createdAccount);
        Account seller = accountRepository.findById(createdAccount.getId()).orElse(null);
        assertThat(seller).isNotNull();
        assertThat(seller.getRole()).isEqualTo(Roles.SELLER);
    }
}
