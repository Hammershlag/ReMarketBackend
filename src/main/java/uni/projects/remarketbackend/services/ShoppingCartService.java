package uni.projects.remarketbackend.services;

import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uni.projects.remarketbackend.dao.*;
import uni.projects.remarketbackend.dto.ShoppingCartDto;
import uni.projects.remarketbackend.dto.order.OrderRequest;
import uni.projects.remarketbackend.exceptions.exceptions.AuthenticationException;
import uni.projects.remarketbackend.exceptions.exceptions.ClientException;
import uni.projects.remarketbackend.exceptions.exceptions.NotFoundException;
import uni.projects.remarketbackend.models.ShoppingCart;
import uni.projects.remarketbackend.models.account.Account;
import uni.projects.remarketbackend.models.listing.Listing;
import uni.projects.remarketbackend.models.order.Address;
import uni.projects.remarketbackend.models.order.Order;
import uni.projects.remarketbackend.models.order.OrderStatus;
import uni.projects.remarketbackend.models.order.payment.Payment;
import uni.projects.remarketbackend.models.order.payment.PaymentStatus;

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
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private StripeService stripeService;

    public ShoppingCartDto getShoppingCart(HttpServletRequest request) throws AuthenticationException {

        Account account = accountService.getAccount(request);
        if (account == null) {
            throw new AuthenticationException("User is not authenticated.");
        }

        if (account.getShoppingCart() == null) {
            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setListings(new ArrayList<>());
            shoppingCartRepository.save(shoppingCart);
            account.setShoppingCart(shoppingCart);
            accountRepository.save(account);
        }

        return ShoppingCartDto.valueFrom(account.getShoppingCart());
    }

    @SneakyThrows
    public String checkout(HttpServletRequest request, OrderRequest orderDto) throws AuthenticationException, NotFoundException, ClientException {

        Account account = accountService.getAccount(request);
        if (account == null) {
            throw new AuthenticationException("User is not authenticated.");
        }

        ShoppingCart shoppingCart = account.getShoppingCart();
//        if (shoppingCart == null || shoppingCart.getListings().isEmpty()) {
//            throw new NotFoundException("Shopping cart is empty or not found.");
//        }

        if (orderDto.getStreet() == null || orderDto.getStreet().isBlank()) {
            throw new ClientException("Street address cannot be empty.");
        }
        if (orderDto.getCity() == null || orderDto.getCity().isBlank()) {
            throw new ClientException("City cannot be empty.");
        }
        if (orderDto.getCountry() == null || orderDto.getCountry().isBlank()) {
            throw new ClientException("Country cannot be empty.");
        }
        if (orderDto.getPaymentMethod() == null) {
            throw new ClientException("Payment method cannot be null.");
        }

        Order order = new Order();

        Address address = new Address();
        address.setStreet(orderDto.getStreet());
        address.setCity(orderDto.getCity());
        address.setState(orderDto.getState());
        address.setZipCode(orderDto.getZipCode());
        address.setCountry(orderDto.getCountry());
        addressRepository.save(address);

        order.setAddress(address);
        order.setShippingMethod(orderDto.getShippingMethod());
        order.setBuyer(account);
        order.setListings(shoppingCart.getListings());

        Payment payment = new Payment();
        payment.setTotal(shoppingCart.getListings().stream().mapToDouble(Listing::getPrice).sum());
        payment.setPaymentMethod(orderDto.getPaymentMethod());
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setCurrency(orderDto.getCurrency());
        paymentRepository.save(payment);

//        stripeService.createPayment(payment, account);
        String sessionId = stripeService.createCheckoutSession(payment.getTotal().longValue());

        order.setPayment(payment);
        order.setOrderStatus(OrderStatus.SHIPPING);
        orderRepository.save(order);

        shoppingCart.setListings(new ArrayList<>());
        shoppingCartRepository.save(shoppingCart);

        return sessionId;
    }
}
