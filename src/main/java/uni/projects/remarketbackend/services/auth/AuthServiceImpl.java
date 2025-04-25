package uni.projects.remarketbackend.services.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import uni.projects.remarketbackend.config.jwt.JwtTokenProvider;
import uni.projects.remarketbackend.dao.AccountRepository;
import uni.projects.remarketbackend.dto.AccountDto;
import uni.projects.remarketbackend.dto.auth.JwtAuthResponse;
import uni.projects.remarketbackend.dto.auth.LoginDto;
import uni.projects.remarketbackend.exceptions.exceptions.AuthenticationException;
import uni.projects.remarketbackend.exceptions.exceptions.JwtTokenException;
import uni.projects.remarketbackend.models.Account;
import uni.projects.remarketbackend.models.Status;

import javax.security.auth.login.LoginException;
import java.time.LocalDateTime;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 07.02.2025
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public JwtAuthResponse login(LoginDto loginDto) throws LoginException, AuthenticationException {

        Authentication authentication;;

        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getUsernameOrEmail(), loginDto.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            throw new AuthenticationException("Invalid username or password");
        }


        String accessToken = jwtTokenProvider.generateAccessToken(authentication.getName());
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication.getName());


        Account account = accountRepository.findByUsernameOrEmail(loginDto.getUsernameOrEmail(), loginDto.getUsernameOrEmail())
                .orElseThrow(() -> new AuthenticationException("User not found"));

        if (account.getStatus() != Status.ACTIVE)
            throw new AuthenticationException("User is not active");

        account.setLastLogin(LocalDateTime.now());
        accountRepository.save(account);

        return new JwtAuthResponse(accessToken, refreshToken);
    }

    public String refreshToken(String refreshToken) throws JwtTokenException {
        if (jwtTokenProvider.validateToken(refreshToken)) {
            String username = jwtTokenProvider.getUsername(refreshToken);
            return jwtTokenProvider.generateAccessToken(username);
        }
        return null;
    }

    @Override
    public JwtAuthResponse getTokensAfterEdit(AccountDto accountDto) throws JwtTokenException {

        String accessToken = null;
        String refreshToken = null;
        String verificationToken = null;

        try {
            if (accountDto.getUsername() != null) {
                accessToken = jwtTokenProvider.generateAccessToken(accountDto.getUsername());
                refreshToken = jwtTokenProvider.generateRefreshToken(accountDto.getUsername());
            }

            return new JwtAuthResponse(accessToken, refreshToken, verificationToken);
        } catch (Exception e) {
            throw new JwtTokenException("Invalid refresh token");
        }
    }
}
