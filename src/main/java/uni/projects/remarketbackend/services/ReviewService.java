package uni.projects.remarketbackend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uni.projects.remarketbackend.dao.ReviewRepository;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 28.04.2025
 */

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

}
