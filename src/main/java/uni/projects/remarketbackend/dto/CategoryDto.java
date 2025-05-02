package uni.projects.remarketbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uni.projects.remarketbackend.models.Category;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 27.04.2025
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "CategoryDto", description = "Data transfer object for category details")
public class CategoryDto {

    @Schema(description = "Unique identifier of the category", example = "1")
    private Long id;

    @Schema(description = "Name of the category", example = "Electronics")
    private String name;

    public static CategoryDto valueFrom(Category category) {
        return new CategoryDto(category.getId(),category.getName());
    }

    public Category convertTo() {
        Category category = new Category();
        category.setName(this.name);
        category.setId(this.id);

        return category;
    }
}

