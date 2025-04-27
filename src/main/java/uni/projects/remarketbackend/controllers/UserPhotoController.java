package uni.projects.remarketbackend.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uni.projects.remarketbackend.dto.PhotoDto;
import uni.projects.remarketbackend.models.account.Account;
import uni.projects.remarketbackend.services.AccountService;
import uni.projects.remarketbackend.services.UserPhotoService;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 27.04.2025
 */

@RestController
@RequestMapping("/api/photo/user")
public class UserPhotoController {

    @Autowired
    private UserPhotoService userPhotoService;
    @Autowired
    private AccountService accountService;

    @PostMapping()
    @SneakyThrows
    @Transactional
    public ResponseEntity<PhotoDto> uploadPhoto(HttpServletRequest request, @RequestBody MultipartFile photo) {
        Account account = accountService.getAccount(request);
        PhotoDto photoDto = userPhotoService.uploadPhoto(photo, account);

        return ResponseEntity.ok(photoDto);
    }

    @GetMapping()
    @Transactional
    public ResponseEntity<PhotoDto> getPhoto(HttpServletRequest request) {
        Account account = accountService.getAccount(request);
        PhotoDto photoDto = userPhotoService.getPhoto(account);

        return ResponseEntity.ok(photoDto);
    }

    @DeleteMapping()
    @Transactional
    public ResponseEntity<Void> deletePhoto(HttpServletRequest request) {
        Account account = accountService.getAccount(request);
        userPhotoService.deletePhoto(account);

        return ResponseEntity.noContent().build();
    }

    @PutMapping()
    @SneakyThrows
    @Transactional
    public ResponseEntity<PhotoDto> updatePhoto(HttpServletRequest request, @RequestBody MultipartFile photo) {
        Account account = accountService.getAccount(request);
        PhotoDto photoDto = userPhotoService.updatePhoto(photo, account);

        return ResponseEntity.ok(photoDto);
    }
}
