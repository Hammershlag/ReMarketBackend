package uni.projects.remarketbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uni.projects.remarketbackend.models.Photo;
import uni.projects.remarketbackend.models.account.Account;

import java.util.Base64;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 27.04.2025
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "PhotoDto", description = "Data transfer object for photo details")
public class PhotoDto {

    @Schema(description = "Unique identifier of the photo", example = "1")
    private Long id;

    @Schema(description = "Base64 encoded data of the photo", example = "iVBORw0KGgoAAAANSUhEUgAA...")
    private String data;

    @Schema(description = "Username of the uploader", example = "uploader123")
    private String uploader;

    // Static methods for conversion
    public static PhotoDto valueFrom(Photo photo) {
        return new PhotoDto(
                photo.getId(),
                Base64.getEncoder().encodeToString(photo.getData()), // Encode to Base64
                photo.getUploader().getUsername()
        );
    }

    public static PhotoDto onlyId(Photo photo) {
        return new PhotoDto(
                photo.getId(),
                "",
                photo.getUploader().getUsername()
        );
    }

    public Photo convertTo() {
        Photo photo = new Photo();
        photo.setId(getId() != null ? getId().longValue() : null);
        photo.setData(Base64.getDecoder().decode(getData())); // Decode from Base64
        photo.setUploader(null);
        return photo;
    }
}
