package uni.projects.remarketbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uni.projects.remarketbackend.models.Photo;

import java.util.Base64;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 27.04.2025
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhotoDto {

    @Schema(description = "The ID of the image (should be left empty when created, will be returned but ignored if user passes it)", example = "1")
    private Integer id;

    @Schema(description = "The Base64 encoded data of the image", example = "iVBORw0KGgoAAAANSUhEUgAA...")
    private String data;

    // Static methods for conversion
    public static PhotoDto valueFrom(Photo photo) {
        return new PhotoDto(
                photo.getId().intValue(),
                Base64.getEncoder().encodeToString(photo.getData()) // Encode to Base64
        );
    }

    public static PhotoDto onlyId(Photo photo) {
        return new PhotoDto(
                photo.getId().intValue(),
                null
        );
    }

    public Photo convertTo() {
        Photo photo = new Photo();
        photo.setId(getId() != null ? getId().longValue() : null);
        photo.setData(Base64.getDecoder().decode(getData())); // Decode from Base64
        return photo;
    }
}
