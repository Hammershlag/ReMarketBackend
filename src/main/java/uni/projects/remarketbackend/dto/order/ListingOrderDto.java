package uni.projects.remarketbackend.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uni.projects.remarketbackend.models.order.ListingOrder;
import uni.projects.remarketbackend.models.order.OrderStatus;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 07.06.2025
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ListingOrderDto", description = "Data transfer object for listing in order")
public class ListingOrderDto {

    @Schema(description = "Unique identifier of the listing order", example = "1")
    private Long id;

    @Schema(description = "Status of the listing in the order", example = "SHIPPING")
    private OrderStatus listingStatus;

    @Schema(description = "Listing ID", example = "42")
    private Long listingId;

    public static ListingOrderDto valueFrom(ListingOrder listingOrder) {
        return new ListingOrderDto(
                listingOrder.getId(),
                listingOrder.getListingStatus(),
                listingOrder.getListing().getId()
        );
    }
}
