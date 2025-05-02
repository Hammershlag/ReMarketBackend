package uni.projects.remarketbackend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uni.projects.remarketbackend.dao.AccountRepository;
import uni.projects.remarketbackend.dao.PhotoRepository;
import uni.projects.remarketbackend.dto.PhotoDto;
import uni.projects.remarketbackend.exceptions.exceptions.ClientException;
import uni.projects.remarketbackend.models.Photo;
import uni.projects.remarketbackend.models.account.Account;

import java.time.LocalDateTime;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 27.04.2025
 */

@Service
public class UserPhotoService {

    @Autowired
    private PhotoRepository photoRepository;
    @Autowired
    private AccountRepository accountRepository;

    public PhotoDto uploadPhoto(MultipartFile photo, Account account) throws ClientException {

        if (photo.isEmpty()) {
            throw new ClientException("Photo file is empty");
        }
        if (photo.getSize() > 5 * 1024 * 1024) { // 5MB limit
            throw new ClientException("Photo file size exceeds the limit of 5MB");
        }
        if (!photo.getContentType().startsWith("image/")) {
            throw new ClientException("File is not an image");
        }
        if (account.getPhoto() != null) {
            throw new ClientException("User already has a photo");
        }

        Photo photoEntity = new Photo();
        try {
            photoEntity.setData(photo.getBytes());
            photoEntity.setUploader(account);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload photo", e);
        }
        Photo savedPhoto = photoRepository.save(photoEntity);
        account.setPhoto(savedPhoto);
        accountRepository.save(account);
        return PhotoDto.valueFrom(savedPhoto);
    }

    public PhotoDto getPhoto(Account account) throws ClientException {
        if (account.getPhoto() == null) {
            throw new ClientException("User has no photo");
        }
        return account.getPhoto() != null ? PhotoDto.valueFrom(account.getPhoto()) : null;
    }

    public void deletePhoto(Account account) throws ClientException {

        if (account.getPhoto() == null) {
            throw new ClientException("User has no photo");
        }
        photoRepository.deleteById(account.getPhoto().getId());
        account.setPhoto(null);
        account.setUpdatedAt(LocalDateTime.now());

    }

    public PhotoDto updatePhoto(MultipartFile photo, Account account) throws ClientException {

        if (photo.isEmpty()) {
            throw new ClientException("Photo file is empty");
        }
        if (photo.getSize() > 5 * 1024 * 1024) { // 5MB limit
            throw new ClientException("Photo file size exceeds the limit of 5MB");
        }
        if (!photo.getContentType().startsWith("image/")) {
            throw new ClientException("File is not an image");
        }
        if (account.getPhoto() != null) {
            try {
                account.getPhoto().setData(photo.getBytes());
                photoRepository.save(account.getPhoto());
                return PhotoDto.valueFrom(account.getPhoto());
            } catch (Exception e) {
                throw new RuntimeException("Failed to update photo", e);
            }
        }
        return null;
    }
}
