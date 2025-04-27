package uni.projects.remarketbackend.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import uni.projects.remarketbackend.models.Photo;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 27.04.2025
 */
public interface PhotoRepository extends JpaRepository<Photo, Long> {
    // Custom query methods can be defined here if needed
}
