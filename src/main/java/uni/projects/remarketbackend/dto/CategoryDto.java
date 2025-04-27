package uni.projects.remarketbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uni.projects.remarketbackend.models.Category;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 27.04.2025
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {

    private Long id;
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
