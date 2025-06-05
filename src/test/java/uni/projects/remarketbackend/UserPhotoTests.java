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
}