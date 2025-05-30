package uni.projects.remarketbackend.services;

import com.stripe.model.tax.Registration;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uni.projects.remarketbackend.dao.ListingRepository;
import uni.projects.remarketbackend.dao.PhotoRepository;
import uni.projects.remarketbackend.dto.PhotoDto;
import uni.projects.remarketbackend.exceptions.exceptions.ClientException;
import uni.projects.remarketbackend.exceptions.exceptions.AuthenticationException;
import uni.projects.remarketbackend.exceptions.exceptions.NotFoundException;
import uni.projects.remarketbackend.models.Photo;
import uni.projects.remarketbackend.models.account.Account;
import uni.projects.remarketbackend.models.listing.Listing;

import java.util.Base64;
import java.util.List;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 27.04.2025
 */

@Service
public class ListingPhotoService {

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private ListingRepository listingRepository;
    @Autowired
    private AccountService accountService;

    public PhotoDto uploadPhoto(MultipartFile photo, Account account) throws ClientException, AuthenticationException {
        if (account == null) {
            throw new AuthenticationException("User is not authenticated.");
        }

        if (photo.isEmpty()) {
            throw new ClientException("Photo file is empty.");
        }
        if (photo.getSize() > 5 * 1024 * 1024) { // 5MB limit
            throw new ClientException("Photo file size exceeds the limit of 5MB.");
        }
        if (!photo.getContentType().startsWith("image/")) {
            throw new ClientException("File is not an image.");
        }

        Photo photoEntity = new Photo();
        try {
            photoEntity.setData(photo.getBytes());
            photoEntity.setUploader(account);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload photo.", e);
        }
        Photo savedPhoto = photoRepository.save(photoEntity);
        return PhotoDto.valueFrom(savedPhoto);
    }

    public PhotoDto getPhoto(Long id) throws ClientException, NotFoundException {
        Photo photo = photoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Photo not found."));
        return PhotoDto.valueFrom(photo);
    }

    @Transactional
    public Photo createPhoto(PhotoDto photoDto) throws ClientException, AuthenticationException {
//        Photo photoEntity = new Photo();
//        try {
//            photoEntity.setData(Base64.getDecoder().decode(photoDto.getData()));
//            photoEntity.setUploader(photoDto.getUploader());
//        }
        Photo photo = new Photo();
        try {
            photo.setData(Base64.getDecoder().decode(photoDto.getData()));
            photo.setUploader(accountService.getAccount(photoDto.getUploader()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload photo.", e);
        }
        Photo savedPhoto = photoRepository.save(photo);
        return savedPhoto;
    }

    public void deletePhoto(Long id, Long listingId, HttpServletRequest request) throws ClientException, AuthenticationException, NotFoundException {
        Account account = accountService.getAccount(request);
        if (account == null) {
            throw new AuthenticationException("User is not authenticated.");
        }

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new NotFoundException("Listing not found."));
        Photo photo = photoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Photo not found."));

        if (!photo.getUploader().equals(account)) {
            throw new ClientException("You are not the uploader of this photo.");
        }

        if (!listing.getPhotos().contains(photo)) {
            throw new ClientException("Photo not found in listing.");
        }

        listing.getPhotos().remove(photo);
        photoRepository.delete(photo);
        listingRepository.save(listing);
    }

    public List<PhotoDto> getListingPhotos(Long listingId) throws ClientException, NotFoundException {
        return listingRepository.findById(listingId)
                .map(Listing::getPhotos)
                .orElseThrow(() -> new NotFoundException("Listing not found."))
                .stream()
                .map(PhotoDto::valueFrom)
                .toList();
    }
}

