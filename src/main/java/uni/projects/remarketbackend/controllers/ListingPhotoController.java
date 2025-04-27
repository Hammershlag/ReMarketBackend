package uni.projects.remarketbackend.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import uni.projects.remarketbackend.dto.PhotoDto;
import uni.projects.remarketbackend.models.account.Account;
import uni.projects.remarketbackend.services.AccountService;
import uni.projects.remarketbackend.services.ListingPhotoService;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 27.04.2025
 */

@RestController
@RequestMapping("/api/photo/listing")
public class ListingPhotoController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private ListingPhotoService listingPhotoService;


    @PostMapping()
    @SneakyThrows
    @Transactional
    public ResponseEntity<Long> uploadPhoto(HttpServletRequest request, @RequestBody MultipartFile photo) {
        Account account = accountService.getAccount(request);
        PhotoDto photoDto = listingPhotoService.uploadPhoto(photo, account);

        return ResponseEntity.ok(photoDto.getId());
    }

}
