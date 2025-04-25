package uni.projects.remarketbackend.services.auth;

import uni.projects.remarketbackend.dto.AccountDto;
import uni.projects.remarketbackend.dto.auth.JwtAuthResponse;
import uni.projects.remarketbackend.dto.auth.LoginDto;
import uni.projects.remarketbackend.exceptions.exceptions.AuthenticationException;
import uni.projects.remarketbackend.exceptions.exceptions.JwtTokenException;

import javax.security.auth.login.LoginException;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 07.02.2025
 */
public interface AuthService {

    JwtAuthResponse login(LoginDto loginDto) throws LoginException, AuthenticationException;
    String refreshToken(String refreshToken) throws JwtTokenException;
    JwtAuthResponse getTokensAfterEdit(AccountDto accountDto) throws JwtTokenException;
}
