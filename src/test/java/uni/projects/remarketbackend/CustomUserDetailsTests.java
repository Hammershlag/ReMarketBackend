package uni.projects.remarketbackend;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import uni.projects.remarketbackend.dao.AccountRepository;
import uni.projects.remarketbackend.dto.AccountDto;
import uni.projects.remarketbackend.models.account.Account;
import uni.projects.remarketbackend.models.account.Roles;
import uni.projects.remarketbackend.models.account.Status;
import uni.projects.remarketbackend.services.AccountService;
import uni.projects.remarketbackend.services.CustomUserDetailsService;
import java.time.LocalDateTime;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CustomUserDetailsTests {

    @Autowired private CustomUserDetailsService customUserDetailsService;
    @Autowired private AccountRepository accountRepository;
    @Autowired private AccountService accountService;
    @Autowired private JdbcTemplate jdbcTemplate;

    private Account testAccount;
    private String testUsername = "testuser";
    private String testEmail = "testuser@example.com";
    private String testPassword = "Password123!";

    @BeforeAll
    void setup() throws Exception {
        AccountDto accountDto = new AccountDto(testUsername, testPassword, testEmail, Roles.USER.getRole());
        testAccount = accountService.createUser(accountDto);
        testAccount.setStatus(Status.ACTIVE);
        testAccount.setCreatedAt(LocalDateTime.now());
        testAccount.setUpdatedAt(LocalDateTime.now());
        accountRepository.save(testAccount);
    }

    @AfterAll
    void cleanup() {
        accountRepository.deleteAll();
        jdbcTemplate.execute("ALTER TABLE accounts ALTER COLUMN id RESTART WITH 1");
    }

    @Test
    @Transactional
    @Order(1)
    void testLoadUserByUsernameSuccessfully() {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(testUsername);

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(testUsername);
        assertThat(userDetails.getPassword()).isEqualTo(testAccount.getPassword());

        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertThat(authorities).hasSize(1);
        assertThat(authorities.iterator().next().getAuthority()).isEqualTo(Roles.USER.getRole());
    }

    @Test
    @Order(2)
    void testLoadUserByUsernameThrowsExceptionWhenUsernameNotFound() {
        String nonExistentUsername = "nonexistentuser";

        Exception ex = assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername(nonExistentUsername);
        });

        assertThat(ex.getMessage()).isEqualTo("User not exists by Username or Email");
    }

    @Test
    @Order(3)
    void testLoadUserByEmailThrowsExceptionWhenEmailNotFound() {
        String nonExistentEmail = "nonexistent@example.com";

        Exception ex = assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername(nonExistentEmail);
        });

        assertThat(ex.getMessage()).isEqualTo("User not exists by Username or Email");
    }
}