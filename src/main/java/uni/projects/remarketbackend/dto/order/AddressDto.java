package uni.projects.remarketbackend.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uni.projects.remarketbackend.models.order.Address;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 28.04.2025
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressDto {
    private Long id;
    private String street;
    private String city;
    private String state;
    private String zipCode;
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
