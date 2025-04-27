package uni.projects.remarketbackend.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uni.projects.remarketbackend.models.listing.Listing;

import java.util.List;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 27.04.2025
 */

@Entity
@Table(name = "wishlists")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    @JoinTable(name = "wishlists_listings",
            joinColumns = @JoinColumn(name = "wishlist_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "listing_id", referencedColumnName = "id")
    )
    private List<Listing> listings;

}
