package uni.projects.remarketbackend;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import uni.projects.remarketbackend.dao.AccountRepository;
import uni.projects.remarketbackend.dto.AccountDto;
import uni.projects.remarketbackend.dto.admin.ExtendedAccountDto;
import uni.projects.remarketbackend.exceptions.exceptions.NotFoundException;
import uni.projects.remarketbackend.models.account.Account;
import uni.projects.remarketbackend.models.account.Roles;
import uni.projects.remarketbackend.models.account.Status;
import uni.projects.remarketbackend.services.AccountService;
import uni.projects.remarketbackend.services.AdminAccountService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AdminAccountTests {

    @Autowired private AdminAccountService adminAccountService;
    @Autowired private AccountService accountService;
    @Autowired private AccountRepository accountRepository;
    @Autowired private JdbcTemplate jdbcTemplate;

    private Account testAccount;

    @BeforeAll
    void setup() throws Exception {
        AccountDto dto = new AccountDto("adminTest", "Pass1234!", "adminTest@example.com", Roles.USER.getRole());
        testAccount = accountService.createUser(dto);
    }

    @AfterAll
    void cleanup() {
        accountRepository.deleteAll();
        jdbcTemplate.execute("ALTER TABLE accounts ALTER COLUMN id RESTART WITH 1");
    }

    @Test
    @Order(1)
    void testGetAllAccounts() {
        Page<ExtendedAccountDto> page = adminAccountService.getAllAccounts(PageRequest.of(0, 10));
        assertThat(page).isNotNull();
        assertThat(page.getContent()).isNotEmpty();
    }

    @Test
    @Order(2)
    void testGetAccountById() {
        ExtendedAccountDto dto = adminAccountService.getAccountById(testAccount.getId());
        assertThat(dto).isNotNull();
        assertThat(dto.getUsername()).isEqualTo(testAccount.getUsername());
    }

    @Test
    @Order(3)
    void testBlockAndUnblockAccount() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        // Initially active
        assertThat(testAccount.getStatus()).isEqualTo(Status.ACTIVE);

        // Block
        adminAccountService.blockAccount(testAccount.getId(), request);
        Account blocked = accountRepository.findById(testAccount.getId()).orElse(null);
        assertThat(blocked).isNotNull();
        assertThat(blocked.getStatus()).isEqualTo(Status.DISABLED);
        assertThat(blocked.getDisabledAt()).isNotNull();

        // Unblock
        adminAccountService.blockAccount(testAccount.getId(), request);
        Account unblocked = accountRepository.findById(testAccount.getId()).orElse(null);
        assertThat(unblocked).isNotNull();
        assertThat(unblocked.getStatus()).isEqualTo(Status.ACTIVE);
        assertThat(unblocked.getDisabledAt()).isNull();
    }

    @Test
    @Order(4)
    void testBlockAccountNotFoundThrows() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        Long invalidId = 9999L;
        Exception exception = assertThrows(NotFoundException.class, () -> {
            adminAccountService.blockAccount(invalidId, request);
        });
        assertThat(exception.getMessage()).isEqualTo("Account not found");
    }

    @Test
    @Order(5)
    void testGetAccountByIdNotFoundThrows() {
        Long invalidId = 9999L;
        Exception exception = assertThrows(NotFoundException.class, () -> {
            adminAccountService.getAccountById(invalidId);
        });
        assertThat(exception.getMessage()).isEqualTo("Account not found");
    }
}
