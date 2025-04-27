package uni.projects.remarketbackend.services;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uni.projects.remarketbackend.dao.AccountRepository;
import uni.projects.remarketbackend.dao.WishlistRepository;
import uni.projects.remarketbackend.dto.WishlistDto;
import uni.projects.remarketbackend.models.Wishlist;
import uni.projects.remarketbackend.models.account.Account;

import java.util.ArrayList;

import static java.util.Arrays.stream;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 27.04.2025
 */

@Service
public class WishlistService {

    @Autowired
    private AccountService accountService;
    @Autowired
    private WishlistRepository wishlistRepository;
    @Autowired
    private AccountRepository accountRepository;

    public WishlistDto getWishlist(HttpServletRequest request) {
        Account account = accountService.getAccount(request);

        if (account.getWishlist() == null) {
            Wishlist wishlist = new Wishlist();
            wishlist.setListings(new ArrayList<>());
            wishlistRepository.save(wishlist);
            account.setWishlist(wishlist);
            accountRepository.save(account);
        }
        return WishlistDto.valueFrom(account.getWishlist());
    }
}
