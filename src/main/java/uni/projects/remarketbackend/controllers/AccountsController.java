package uni.projects.remarketbackend.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uni.projects.remarketbackend.dto.AccountDto;
import uni.projects.remarketbackend.exceptions.exceptions.NotFoundException;
import uni.projects.remarketbackend.models.account.Account;
import uni.projects.remarketbackend.services.AccountService;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 27.04.2025
 */

@AllArgsConstructor
@RestController
@RequestMapping("/api/accounts")
@Tag(name = "Accounts", description = "Endpoints for managing user accounts. Used for authenticated users" +
        "Keep in mind that all endpoints require a valid JWT token in the Authorization header with a Bearer prefix")
public class AccountsController {

    @Autowired
    private AccountService accountService;

    @Operation(summary = "Get user",
            description = "Gets user data with blurred password",
            tags = {"Accounts"})

    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "User data retrieved successfully", content = @Content(schema = @Schema(implementation = AccountDto.class))),
                    @ApiResponse(responseCode = "406", description = "Something went wrong, message will be provided", content = @Content),
                    @ApiResponse(responseCode = "409", description = "Jwt exception, you have to login or refresh tokens", content = @Content),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
            }
    )
    @GetMapping
    @SneakyThrows
    public ResponseEntity<AccountDto> getAccount(HttpServletRequest request) {

        Account account = accountService.getAccount(request);
        if (account == null)
            throw new NotFoundException("User not found");
        AccountDto accountDto = AccountDto.fromAccount(account);
        return new ResponseEntity<>(accountDto, HttpStatus.OK);
    }

    @Operation(summary = "Change user information",
            description = "Change user information",
            tags = {"Accounts"})
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "User data retrieved successfully", content = @Content(schema = @Schema(implementation = AccountDto.class))),
                    @ApiResponse(responseCode = "406", description = "Something went wrong, message will be provided", content = @Content),
                    @ApiResponse(responseCode = "409", description = "Jwt exception, you have to login or refresh tokens", content = @Content),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
            }
    )
    @PutMapping
    @SneakyThrows
    public ResponseEntity<AccountDto> updateAccount(HttpServletRequest request, @RequestBody AccountDto accountDto) {
        Account currentAccount = accountService.getAccount(request);
        Account account = accountService.updateAccount(currentAccount, accountDto);
        if (account == null)
            throw new NotFoundException("User not found");
        AccountDto updatedAccountDto = AccountDto.fromAccount(account);
        return new ResponseEntity<>(updatedAccountDto, HttpStatus.OK);
    }

    @Operation(summary = "Delete user",
            description = "Delete user",
            tags = {"Accounts"})
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "User deleted successfully", content = @Content),
                    @ApiResponse(responseCode = "406", description = "Something went wrong, message will be provided", content = @Content),
                    @ApiResponse(responseCode = "409", description = "Jwt exception, you have to login or refresh tokens", content = @Content),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
            }
    )
    @DeleteMapping
    @SneakyThrows
    public ResponseEntity<String> deleteAccount(HttpServletRequest request) {
        Account account = accountService.getAccount(request);
        if (account == null)
            throw new NotFoundException("User not found");
        accountService.deleteAccount(account);
        return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
    }

    @Operation(summary = "Become seller",
            description = "Make user a seller. Only users with the role USER can become sellers. Admins and staff cannot downgrade their roles.",
            tags = {"Accounts"})
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "User is now a seller", content = @Content),
                    @ApiResponse(responseCode = "406", description = "Something went wrong, message will be provided", content = @Content),
                    @ApiResponse(responseCode = "409", description = "Jwt exception, you have to login or refresh tokens", content = @Content),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Cannot downgrade role (e.g., admin to seller)", content = @Content)
            }
    )
    @PostMapping("/become-seller")
    @SneakyThrows
    public ResponseEntity<String> becomeSeller(HttpServletRequest request) {
        Account account = accountService.getAccount(request);
        if (account == null)
            throw new NotFoundException("User not found");
        accountService.becomeSeller(account);
        return new ResponseEntity<>("User is now a seller", HttpStatus.OK);
    }

}
