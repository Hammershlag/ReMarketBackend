package uni.projects.remarketbackend;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import uni.projects.remarketbackend.dao.AccountRepository;
import uni.projects.remarketbackend.dao.PhotoRepository;
import uni.projects.remarketbackend.dto.AccountDto;
import uni.projects.remarketbackend.dto.PhotoDto;
import uni.projects.remarketbackend.exceptions.exceptions.ClientException;
import uni.projects.remarketbackend.models.Photo;
import uni.projects.remarketbackend.models.account.Account;
import uni.projects.remarketbackend.models.account.Roles;
import uni.projects.remarketbackend.models.account.Status;
import uni.projects.remarketbackend.services.AccountService;
import uni.projects.remarketbackend.services.UserPhotoService;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserPhotoTests {

    @Autowired private UserPhotoService userPhotoService;
    @Autowired private PhotoRepository photoRepository;
    @Autowired private AccountRepository accountRepository;
    @Autowired private AccountService accountService;

    private Account testAccount;
    private Account accountWithPhoto;
    private Photo testPhoto;

    @BeforeAll
    void setup() throws Exception {
        AccountDto accountDto = new AccountDto("userphototest", "Password123!", "userphoto@example.com", Roles.USER.getRole());
        testAccount = accountService.createUser(accountDto);
        testAccount.setStatus(Status.ACTIVE);
        testAccount.setCreatedAt(LocalDateTime.now());
        testAccount.setUpdatedAt(LocalDateTime.now());
        accountRepository.save(testAccount);

        AccountDto accountWithPhotoDto = new AccountDto("userwithphoto", "Password123!", "userwithphoto@example.com", Roles.USER.getRole());
        accountWithPhoto = accountService.createUser(accountWithPhotoDto);
        accountWithPhoto.setStatus(Status.ACTIVE);
        accountWithPhoto.setCreatedAt(LocalDateTime.now());
        accountWithPhoto.setUpdatedAt(LocalDateTime.now());

        testPhoto = new Photo();
        testPhoto.setData("test photo data".getBytes());
        testPhoto.setUploader(accountWithPhoto);
        testPhoto = photoRepository.save(testPhoto);

        accountWithPhoto.setPhoto(testPhoto);
        accountRepository.save(accountWithPhoto);
    }

    @Test
    @Transactional
    @Order(1)
    void testUploadPhotoSuccessfully() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "photo", "test.jpg", "image/jpeg", "test content".getBytes()
        );

        PhotoDto result = userPhotoService.uploadPhoto(mockFile, testAccount);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getUploader()).isEqualTo("userphototest");

        Account updatedAccount = accountRepository.findById(testAccount.getId()).orElse(null);
        assertThat(updatedAccount).isNotNull();
        assertThat(updatedAccount.getPhoto()).isNotNull();
        assertThat(updatedAccount.getPhoto().getId()).isEqualTo(result.getId());
    }

    @Test
    @Order(2)
    void testUploadPhotoFailsWhenFileEmpty() {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "photo", "test.jpg", "image/jpeg", new byte[0]
        );

        Exception ex = assertThrows(ClientException.class, () -> {
            userPhotoService.uploadPhoto(emptyFile, testAccount);
        });

        assertThat(ex.getMessage()).isEqualTo("Photo file is empty");
    }

    @Test
    @Order(3)
    void testUploadPhotoFailsWhenFileTooLarge() {
        byte[] largeData = new byte[6 * 1024 * 1024];
        MockMultipartFile largeFile = new MockMultipartFile(
                "photo", "test.jpg", "image/jpeg", largeData
        );

        Exception ex = assertThrows(ClientException.class, () -> {
            userPhotoService.uploadPhoto(largeFile, testAccount);
        });

        assertThat(ex.getMessage()).isEqualTo("Photo file size exceeds the limit of 5MB");
    }

    @Test
    @Order(4)
    void testUploadPhotoFailsWhenNotImage() {
        MockMultipartFile textFile = new MockMultipartFile(
                "photo", "test.txt", "text/plain", "test content".getBytes()
        );

        Exception ex = assertThrows(ClientException.class, () -> {
            userPhotoService.uploadPhoto(textFile, testAccount);
        });

        assertThat(ex.getMessage()).isEqualTo("File is not an image");
    }

    @Test
    @Order(5)
    void testUploadPhotoFailsWhenUserAlreadyHasPhoto() {
        MockMultipartFile mockFile = new MockMultipartFile(
                "photo", "test.jpg", "image/jpeg", "test content".getBytes()
        );

        Exception ex = assertThrows(ClientException.class, () -> {
            userPhotoService.uploadPhoto(mockFile, accountWithPhoto);
        });

        assertThat(ex.getMessage()).isEqualTo("User already has a photo");
    }

    @Test
    @Transactional(readOnly = true)
    @Order(6)
    void testGetPhotoSuccessfully() throws Exception {
        PhotoDto result = userPhotoService.getPhoto(accountWithPhoto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testPhoto.getId());
        assertThat(result.getUploader()).isEqualTo("userwithphoto");
    }

    @Test
    @Order(7)
    void testGetPhotoFailsWhenUserHasNoPhoto() {
        AccountDto freshAccountDto = new AccountDto("nophotouser", "Password123!", "nophoto@example.com", Roles.USER.getRole());
        Account freshAccount;
        try {
            freshAccount = accountService.createUser(freshAccountDto);
            freshAccount.setStatus(Status.ACTIVE);
            freshAccount.setCreatedAt(LocalDateTime.now());
            freshAccount.setUpdatedAt(LocalDateTime.now());
            accountRepository.save(freshAccount);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Exception ex = assertThrows(ClientException.class, () -> {
            userPhotoService.getPhoto(freshAccount);
        });

        assertThat(ex.getMessage()).isEqualTo("User has no photo");
    }

    @Test
    @Transactional
    @Order(8)
    void testDeletePhotoSuccessfully() throws Exception {
        AccountDto deleteTestAccountDto = new AccountDto("deletetest", "Password123!", "deletetest@example.com", Roles.USER.getRole());
        Account deleteTestAccount = accountService.createUser(deleteTestAccountDto);
        deleteTestAccount.setStatus(Status.ACTIVE);
        deleteTestAccount.setCreatedAt(LocalDateTime.now());
        deleteTestAccount.setUpdatedAt(LocalDateTime.now());

        Photo photoToDelete = new Photo();
        photoToDelete.setData("delete test data".getBytes());
        photoToDelete.setUploader(deleteTestAccount);
        photoToDelete = photoRepository.save(photoToDelete);

        deleteTestAccount.setPhoto(photoToDelete);
        deleteTestAccount = accountRepository.save(deleteTestAccount);

        Long photoId = photoToDelete.getId();

        userPhotoService.deletePhoto(deleteTestAccount);

        assertThat(photoRepository.findById(photoId)).isEmpty();

        Account updatedAccount = accountRepository.findById(deleteTestAccount.getId()).orElse(null);
        assertThat(updatedAccount).isNotNull();
        assertThat(updatedAccount.getPhoto()).isNull();
    }

    @Test
    @Order(9)
    void testDeletePhotoFailsWhenUserHasNoPhoto() {
        AccountDto freshAccountDto = new AccountDto("nodeleteuser", "Password123!", "nodelete@example.com", Roles.USER.getRole());
        Account freshAccount;
        try {
            freshAccount = accountService.createUser(freshAccountDto);
            freshAccount.setStatus(Status.ACTIVE);
            freshAccount.setCreatedAt(LocalDateTime.now());
            freshAccount.setUpdatedAt(LocalDateTime.now());
            accountRepository.save(freshAccount);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Exception ex = assertThrows(ClientException.class, () -> {
            userPhotoService.deletePhoto(freshAccount);
        });

        assertThat(ex.getMessage()).isEqualTo("User has no photo");
    }


    @Test
    @Transactional
    @Order(10)
    void testUpdatePhotoSuccessfully() throws Exception {
        MockMultipartFile newPhotoFile = new MockMultipartFile(
                "photo", "updated.jpg", "image/jpeg", "updated content".getBytes()
        );

        PhotoDto result = userPhotoService.updatePhoto(newPhotoFile, accountWithPhoto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testPhoto.getId());
        assertThat(result.getUploader()).isEqualTo("userwithphoto");

        Photo updatedPhoto = photoRepository.findById(testPhoto.getId()).orElse(null);
        assertThat(updatedPhoto).isNotNull();
        assertThat(new String(updatedPhoto.getData())).isEqualTo("updated content");
    }

    @Test
    @Order(11)
    void testUpdatePhotoFailsWhenFileEmpty() {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "photo", "test.jpg", "image/jpeg", new byte[0]
        );

        Exception ex = assertThrows(ClientException.class, () -> {
            userPhotoService.updatePhoto(emptyFile, accountWithPhoto);
        });

        assertThat(ex.getMessage()).isEqualTo("Photo file is empty");
    }

}