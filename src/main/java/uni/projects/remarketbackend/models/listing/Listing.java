package uni.projects.remarketbackend.models.listing;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uni.projects.remarketbackend.models.Category;
import uni.projects.remarketbackend.models.Photo;
import uni.projects.remarketbackend.models.Review;
import uni.projects.remarketbackend.models.account.Account;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 27.04.2025
 */

@Entity
@Table(name = "listings")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Listing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", nullable = false, length = 2000)
    private String description;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ListingStatus status;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "listing_photos",
            joinColumns = @JoinColumn(name = "listing_id"),
            inverseJoinColumns = @JoinColumn(name = "photo_id")
    )
    private List<Photo> photos = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)

    private Account seller;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private Set<Review> reviews = new HashSet<>();

    @Column(name = "average_rating")
    private Float averageRating;



}
