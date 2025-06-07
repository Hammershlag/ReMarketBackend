package uni.projects.remarketbackend.models.order;

import jakarta.persistence.*;
import jdk.jfr.Name;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uni.projects.remarketbackend.models.listing.Listing;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 07.06.2025
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "listing_orders")
@Entity
public class ListingOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "listing_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus listingStatus;

    @JoinColumn(name = "listing", nullable = false)
    @ManyToOne(cascade = CascadeType.ALL)
    private Listing listing;

    public ListingOrder(Listing listing) {
        this.listing = listing;
        this.listingStatus = OrderStatus.SHIPPING; // Default status when creating a new ListingOrder
    }

}
