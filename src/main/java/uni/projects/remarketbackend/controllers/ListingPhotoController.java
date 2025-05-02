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
import uni.projects.remarketbackend.services.ListingPhotoService;

import java.util.List;

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

@Tag(name = "Listing Photos", description = "Endpoints for managing listing photos")
@RestController
@RequestMapping("/api/photo/listing")
public class ListingPhotoController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private ListingPhotoService listingPhotoService;

    @Operation(summary = "Upload a photo", description = "Upload a photo for a listing.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Photo successfully uploaded",
                         content = @Content(schema = @Schema(implementation = Long.class))),
            @ApiResponse(responseCode = "400", description = "Invalid photo file or missing fields",
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class))),
            @ApiResponse(responseCode = "406", description = "Authentication required",
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class)))
    })
    @PostMapping()
    @SneakyThrows
    @Transactional
    public ResponseEntity<Long> uploadPhoto(HttpServletRequest request, @RequestBody MultipartFile photo) {
        Account account = accountService.getAccount(request);
        PhotoDto photoDto = listingPhotoService.uploadPhoto(photo, account);

        return ResponseEntity.ok(photoDto.getId());
    }

    @Operation(summary = "Get a photo by ID", description = "Retrieve a photo by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Photo successfully retrieved",
                         content = @Content(schema = @Schema(implementation = PhotoDto.class))),
            @ApiResponse(responseCode = "404", description = "Photo not found",
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class)))
    })
    @GetMapping("/{id}")
    @SneakyThrows
    @Transactional
    public ResponseEntity<PhotoDto> getPhoto(@PathVariable Long id) {
        PhotoDto photo = listingPhotoService.getPhoto(id);
        return ResponseEntity.ok(photo);
    }

    @Operation(summary = "Get photos for a listing", description = "Retrieve all photos for a specific listing.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Photos successfully retrieved",
                         content = @Content(schema = @Schema(implementation = PhotoDto.class))),
            @ApiResponse(responseCode = "404", description = "Listing not found",
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class)))
    })
    @GetMapping("/listing/{listingId}")
    @SneakyThrows
    @Transactional
    public ResponseEntity<List<PhotoDto>> getListingPhotos(@PathVariable Long listingId) {
        List<PhotoDto> photos = listingPhotoService.getListingPhotos(listingId);
        return ResponseEntity.ok(photos);
    }


}
