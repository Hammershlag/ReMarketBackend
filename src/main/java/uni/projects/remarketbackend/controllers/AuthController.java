package uni.projects.remarketbackend.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uni.projects.remarketbackend.dto.AccountDto;
import uni.projects.remarketbackend.dto.auth.JwtAuthResponse;
import uni.projects.remarketbackend.dto.auth.LoginDto;
import uni.projects.remarketbackend.dto.auth.RefreshTokenRequest;
import uni.projects.remarketbackend.exceptions.exceptions.AuthenticationException;
import uni.projects.remarketbackend.models.Account;
import uni.projects.remarketbackend.services.AccountService;
import uni.projects.remarketbackend.services.auth.AuthService;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 07.02.2025
 */
@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication. " +
        "Keep in mind that all endpoints other than /login and /register require a valid JWT token in the Authorization header with a Bearer prefix")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private AccountService accountService;

    @Operation(summary = "Login user",
            description = "Login user with provided credentials",
            tags = {"Authentication"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User logged in successfully", content = @Content(schema = @Schema(implementation = JwtAuthResponse.class))),
            @ApiResponse(responseCode = "406", description = "Something went wrong, message will be provided", content = @Content),
    })

    @SneakyThrows
    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> login(@RequestBody LoginDto loginDto) {
        JwtAuthResponse jwtAuthResponse = authService.login(loginDto);
        return new ResponseEntity<>(jwtAuthResponse, HttpStatus.OK);
    }

    @Operation(summary = "Register user",
            description = "Register user with provided credentials",
            tags = {"Authentication"})

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User created successfully", content = @Content),
            @ApiResponse(responseCode = "406", description = "Something went wrong, message will be provided", content = @Content),
    })

    @SneakyThrows
    @PostMapping("/register")
    public ResponseEntity<String> createUser(@RequestBody AccountDto userDto) {

        Account account = accountService.createUser(userDto);

        return ResponseEntity.ok("User created successfully");
    }

    @Operation(summary = "Refresh token",
            description = "Refresh access token with provided refresh token. Only access token and token type are returned in the body of JWTAutResponse",
            tags = {"Authentication"})

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = JwtAuthResponse.class))}),
            @ApiResponse(responseCode = "406", description = "Invalid refresh token - no token provided", content = @Content),
            @ApiResponse(responseCode = "409", description = "JWT token exception - token expired or no user found", content = @Content),
    })

    @SneakyThrows
    @PostMapping("/refresh/token")
    public ResponseEntity<JwtAuthResponse> refresh(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        String accessToken = authService.refreshToken(refreshTokenRequest.getRefreshToken());

        if (accessToken == null) {
            throw new AuthenticationException("Invalid refresh token");
        }

        JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();
        jwtAuthResponse.setAccessToken(accessToken);

        return new ResponseEntity<>(jwtAuthResponse, HttpStatus.OK);
    }

}
