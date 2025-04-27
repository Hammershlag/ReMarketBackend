package uni.projects.remarketbackend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uni.projects.remarketbackend.dao.PhotoRepository;
import uni.projects.remarketbackend.dto.PhotoDto;
import uni.projects.remarketbackend.exceptions.exceptions.ClientException;
import uni.projects.remarketbackend.models.Photo;
import uni.projects.remarketbackend.models.account.Account;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 27.04.2025
 */

@Service
public class ListingPhotoService {

    @Autowired
    private PhotoRepository photoRepository;

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
        return PhotoDto.valueFrom(savedPhoto);
    }

}
