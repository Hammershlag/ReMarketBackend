package uni.projects.remarketbackend.controllers.admin;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uni.projects.remarketbackend.dto.AccountDto;
import uni.projects.remarketbackend.exceptions.ExceptionDetails;
import uni.projects.remarketbackend.services.AdminAccountService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.List;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 27.04.2025
 */

@RestController
@RequestMapping("/api/admin/accounts")
public class AdminAccountController {

    @Autowired
    private AdminAccountService adminUserService;

    @Operation(summary = "Retrieve all accounts", description = "Fetches a paginated list of all accounts.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved accounts"),
            @ApiResponse(responseCode = "406", description = "Authentication exception", 
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class))),
            @ApiResponse(responseCode = "409", description = "JWT token exception", 
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", 
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class)))
    })
    @GetMapping
    public ResponseEntity<Page<AccountDto>> getAllAccounts(Pageable pageable) {
        Page<AccountDto> accounts = adminUserService.getAllAccounts(pageable);
        return ResponseEntity.ok(accounts);
    }

    @Operation(summary = "Block an account", description = "Disables an account by setting its status to DISABLED.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Account successfully blocked"),
            @ApiResponse(responseCode = "404", description = "Account not found", 
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class))),
            @ApiResponse(responseCode = "406", description = "Authentication exception", 
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class))),
            @ApiResponse(responseCode = "409", description = "JWT token exception", 
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", 
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class)))
    })
    @PutMapping("/{id}/block")
    public ResponseEntity<Void> blockAccount(HttpServletRequest request, @PathVariable Long id) {
        adminUserService.blockAccount(id, request);
        return ResponseEntity.noContent().build();
    }

}
