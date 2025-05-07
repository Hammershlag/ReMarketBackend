package uni.projects.remarketbackend.controllers.admin;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uni.projects.remarketbackend.dto.ListingDto;
import uni.projects.remarketbackend.services.AdminListingService;

import java.util.List;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 08.05.2025
 */

@RestController
@RequestMapping("/api/admin/listings")
public class AdminListingsController {

    @Autowired
    private AdminListingService adminListingService;

    @GetMapping("/status/flagged")
    @Transactional
    public ResponseEntity<Page<ListingDto>> getFlaggedListings(@RequestParam(defaultValue = "1") int page,
                                                               @RequestParam(defaultValue = "10") int pageSize) {
        Page<ListingDto> flaggedListings = adminListingService.getFlaggedListings(page, pageSize);
        return ResponseEntity.ok(flaggedListings);
    }

    @PutMapping("{id}/block")
    public ResponseEntity<Void> blockListing(@PathVariable Long id) {
        adminListingService.blockListing(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id}/dismiss")
    public ResponseEntity<Void> dismissListing(@PathVariable Long id) {
        adminListingService.dismissListing(id);
        return ResponseEntity.noContent().build();
    }



}
