package uni.projects.remarketbackend.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uni.projects.remarketbackend.models.order.Address;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 28.04.2025
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "AddressDto", description = "Data transfer object for address details")
public class AddressDto {
    @Schema(description = "Unique identifier of the address", example = "1")
    private Long id;

    @Schema(description = "Street name of the address", example = "123 Main St")
    private String street;

    @Schema(description = "City of the address", example = "New York")
    private String city;

    @Schema(description = "State of the address", example = "NY")
    private String state;

    @Schema(description = "Zip code of the address", example = "10001")
    private String zipCode;

    @Schema(description = "Country of the address", example = "USA")
    private String country;

    public static AddressDto valueFrom(Address address) {
        return new AddressDto(
                address.getId(),
                address.getStreet(),
                address.getCity(),
                address.getState(),
                address.getZipCode(),
                address.getCountry()
        );
    }

    public Address convertTo() {
        Address address = new Address();
        address.setId(this.id);
        address.setStreet(this.street);
        address.setCity(this.city);
        address.setState(this.state);
        address.setZipCode(this.zipCode);
        address.setCountry(this.country);
        return address;
    }
}
