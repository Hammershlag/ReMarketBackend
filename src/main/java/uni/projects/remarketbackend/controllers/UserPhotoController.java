package uni.projects.remarketbackend.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uni.projects.remarketbackend.dto.PhotoDto;
import uni.projects.remarketbackend.exceptions.ExceptionDetails;
import uni.projects.remarketbackend.models.account.Account;
import uni.projects.remarketbackend.services.AccountService;
import uni.projects.remarketbackend.services.UserPhotoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 27.04.2025
 */

@Tag(name = "User Photos", description = "Endpoints for managing user profile photos")
@RestController
@RequestMapping("/api/photo/user")
public class UserPhotoController {

    @Autowired
    private UserPhotoService userPhotoService;
    @Autowired
    private AccountService accountService;

    @Operation(summary = "Upload a user photo", description = "Upload a profile photo for the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Photo successfully uploaded",
                         content = @Content(schema = @Schema(implementation = PhotoDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid photo file or user already has a photo",
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class))),
            @ApiResponse(responseCode = "406", description = "Authentication required",
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class)))
    })
    @PostMapping()
    @SneakyThrows
    @Transactional
    public ResponseEntity<PhotoDto> uploadPhoto(HttpServletRequest request, @RequestBody MultipartFile photo) {
        Account account = accountService.getAccount(request);
        PhotoDto photoDto = userPhotoService.uploadPhoto(photo, account);

        return ResponseEntity.ok(photoDto);
    }

    @Operation(summary = "Get user photo", description = "Retrieve the profile photo of the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Photo successfully retrieved",
                         content = @Content(schema = @Schema(implementation = PhotoDto.class))),
            @ApiResponse(responseCode = "400", description = "User has no photo",
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class))),
            @ApiResponse(responseCode = "406", description = "Authentication required",
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class)))
    })
    @SneakyThrows
    @GetMapping()
    @Transactional
    public ResponseEntity<PhotoDto> getPhoto(HttpServletRequest request) {
        Account account = accountService.getAccount(request);
        PhotoDto photoDto = userPhotoService.getPhoto(account);

        return ResponseEntity.ok(photoDto);
    }

    @Operation(summary = "Delete user photo", description = "Delete the profile photo of the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Photo successfully deleted"),
            @ApiResponse(responseCode = "400", description = "User has no photo",
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class))),
            @ApiResponse(responseCode = "406", description = "Authentication required",
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class)))
    })
    @SneakyThrows
    @DeleteMapping()
    @Transactional
    public ResponseEntity<Void> deletePhoto(HttpServletRequest request) {
        Account account = accountService.getAccount(request);
        userPhotoService.deletePhoto(account);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update user photo", description = "Update the profile photo of the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Photo successfully updated",
                         content = @Content(schema = @Schema(implementation = PhotoDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid photo file",
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class))),
            @ApiResponse(responseCode = "406", description = "Authentication required",
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class)))
    })
    @PutMapping()
    @SneakyThrows
    @Transactional
    public ResponseEntity<PhotoDto> updatePhoto(HttpServletRequest request, @RequestBody MultipartFile photo) {
        Account account = accountService.getAccount(request);
        PhotoDto photoDto = userPhotoService.updatePhoto(photo, account);

        return ResponseEntity.ok(photoDto);
    }
}
