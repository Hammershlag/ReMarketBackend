package uni.projects.remarketbackend.services;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uni.projects.remarketbackend.dao.AccountRepository;
import uni.projects.remarketbackend.dao.ShoppingCartRepository;
import uni.projects.remarketbackend.dto.ShoppingCartDto;
import uni.projects.remarketbackend.models.ShoppingCart;
import uni.projects.remarketbackend.models.account.Account;

import java.util.ArrayList;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 27.04.2025
 */

@Service
public class ShoppingCartService {

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;
    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountRepository accountRepository;

    public ShoppingCartDto getShoppingCart(HttpServletRequest request) {

        Account account = accountService.getAccount(request);
        if (account.getShoppingCart() == null) {
            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setListings(new ArrayList<>());
            shoppingCartRepository.save(shoppingCart);
            account.setShoppingCart(shoppingCart);
            accountRepository.save(account);
        }

        return ShoppingCartDto.valueFrom(account.getShoppingCart());
    }
}
