package uni.projects.remarketbackend.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uni.projects.remarketbackend.config.jwt.JwtAuthenticationFilter;
import uni.projects.remarketbackend.config.jwt.JwtTokenProvider;
import uni.projects.remarketbackend.dao.AccountRepository;
import uni.projects.remarketbackend.dto.AccountDto;
import uni.projects.remarketbackend.exceptions.exceptions.AuthenticationException;
import uni.projects.remarketbackend.models.Photo;
import uni.projects.remarketbackend.models.account.Account;
import uni.projects.remarketbackend.models.account.Roles;
import uni.projects.remarketbackend.models.account.Status;
import uni.projects.remarketbackend.utils.dataVerification.DataVerification;
import uni.projects.remarketbackend.utils.dataVerification.NormalDataVerification;

import java.time.LocalDateTime;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 07.02.2025
 */

@Service
public class AccountService {

    private AccountRepository accountRepository;

    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private DataVerification dataVerification = new NormalDataVerification();

    public AccountService(AccountRepository userRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Account createUser(AccountDto accountDto) throws Exception {

        Roles user_role = Roles.USER;

        Account account = accountDto.toAccount();
        account.setRole(user_role);
        account.setStatus(Status.ACTIVE);
        account.setCreatedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());


        if (!verifyUser(account))
            throw new AuthenticationException("Invalid user data");

        account.setPassword(passwordEncoder.encode(account.getPassword()));
        accountRepository.save(account);

        return account;
    }

    private boolean verifyUser(Account account) throws AuthenticationException {

        if (!dataVerification.verifyUser(account))
            throw new AuthenticationException("Invalid user data");

        if (account.getId() != null){
            if (accountRepository.existsByUsernameAndIdNot(account.getUsername(), account.getId()))
                throw new AuthenticationException("Username already exists");

            if (accountRepository.existsByEmailAndIdNot(account.getEmail(), account.getId()))
                throw new AuthenticationException("Email already exists");

        } else {
            if (accountRepository.existsByUsername(account.getUsername()))
                throw new AuthenticationException("Username already exists");

            if (accountRepository.existsByEmail(account.getEmail()))
                throw new AuthenticationException("Email already exists");
        }

        return true;
    }

    @Transactional
    public Account getAccount(HttpServletRequest request) {
        String token = jwtAuthenticationFilter.getTokenFromRequest(request);
        String usernameOrEmail = jwtTokenProvider.getUsername(token);
        Account account = accountRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail).orElse(null);
        return account;
    }

    public Account updateAccount(Account account, AccountDto accountDto) throws Exception {

        if (accountDto.getUsername() != null) {
            account.setUsername(accountDto.getUsername());
        }
        if (accountDto.getEmail() != null) {
            account.setEmail(accountDto.getEmail());
        }

        if (accountDto.getPassword() != null) {
            account.setPassword(accountDto.getPassword());
        }

        if (!verifyUser(account))
            throw new AuthenticationException("Invalid user data");

        if (accountDto.getPassword() != null) {
            account.setPassword(passwordEncoder.encode(accountDto.getPassword()));
        }

        account.setUpdatedAt(LocalDateTime.now());

        return accountRepository.save(account);
    }

    public void deleteAccount(Account account) {
        account.setStatus(Status.DELETED);
        account.setDeletedAt(LocalDateTime.now());
        accountRepository.save(account);
    }

    public Account getAccount(String usernameOrEmail) {
        return accountRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail).orElse(null);
    }

    public void saveAccount(Account account) {
        accountRepository.save(account);
    }

    public boolean checkPassword(Account account, String password) {
        return passwordEncoder.matches(password, account.getPassword());
    }

    public void becomeSeller(Account account) throws AuthenticationException {
        if (account.getRole() == Roles.SELLER) {
            return;
        }
        if (account.getRole() == Roles.ADMIN || account.getRole() == Roles.STUFF) {
            throw new AuthenticationException("You cannot downgrade your role");
        }
        account.setRole(Roles.SELLER);
        account.setUpdatedAt(LocalDateTime.now());
        accountRepository.save(account);
    }
}
