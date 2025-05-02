package uni.projects.remarketbackend.services;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uni.projects.remarketbackend.dao.CategoryRepository;
import uni.projects.remarketbackend.dto.CategoryDto;
import uni.projects.remarketbackend.exceptions.exceptions.ClientException;
import uni.projects.remarketbackend.models.Category;

import java.util.List;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 27.04.2025
 */

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @SneakyThrows
    public CategoryDto createCategory(CategoryDto categoryDto) {

        // Check if the category already exists
        if (categoryRepository.existsByName(categoryDto.getName())) {
            throw new ClientException("Category already exists");
        }

        // Create a new category entity
        Category category = new Category();
        category.setName(categoryDto.getName());

        // Save the category to the database
        Category savedCategory = categoryRepository.save(category);

        // Convert the saved entity to DTO and return it
        return CategoryDto.valueFrom(savedCategory);

    }

    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(CategoryDto::valueFrom)
                .toList();
    }

    @SneakyThrows
    public CategoryDto getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .map(CategoryDto::valueFrom)
                .orElseThrow(() -> new ClientException("Category not found"));
    }

    public Category getById(Long id) throws ClientException {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ClientException("Category not found"));
    }

    public CategoryDto updateCategory(Long id, CategoryDto categoryDto) throws ClientException {

        // Check if the category exists
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ClientException("Category not found"));

        if (categoryRepository.existsByName(categoryDto.getName())) {
            throw new ClientException("Category already exists");
        }
        // Update the category name
        category.setName(categoryDto.getName());

        // Save the updated category to the database
        Category updatedCategory = categoryRepository.save(category);

        // Convert the updated entity to DTO and return it
        return CategoryDto.valueFrom(updatedCategory);
    }

    @SneakyThrows
    public void deleteCategory(Long id) {

        // Check if the category exists
        if (!categoryRepository.existsById(id)) {
            throw new ClientException("Category not found");
        }

        // Delete the category from the database
        categoryRepository.deleteById(id);
    }
}
