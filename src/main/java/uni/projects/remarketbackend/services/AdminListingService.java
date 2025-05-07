package uni.projects.remarketbackend.services;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uni.projects.remarketbackend.dao.ListingRepository;
import uni.projects.remarketbackend.dto.ListingDto;
import uni.projects.remarketbackend.exceptions.exceptions.ClientException;
import uni.projects.remarketbackend.models.listing.Listing;
import uni.projects.remarketbackend.models.listing.ListingStatus;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 08.05.2025
 */

@Service
public class AdminListingService {

    @Autowired
    private ListingRepository listingRepository;

    public Page<ListingDto> getFlaggedListings(int page, int pageSize) {
        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 10;

        return listingRepository.findAllByStatus(ListingStatus.UNDER_REVIEW, PageRequest.of(page - 1, pageSize))
                .map(ListingDto::valueFrom);
    }

    @SneakyThrows
    public void blockListing(Long id) {
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new ClientException("Listing not found"));
        listing.setStatus(ListingStatus.BLOCKED);
        listingRepository.save(listing);
    }

    @SneakyThrows
    public void dismissListing(Long id) {
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new ClientException("Listing not found"));
        listing.setStatus(ListingStatus.ACTIVE);
        listingRepository.save(listing);
    }
}
