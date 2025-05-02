package uni.projects.remarketbackend.services;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uni.projects.remarketbackend.dao.OrderRepository;
import uni.projects.remarketbackend.dto.order.OrderDto;
import uni.projects.remarketbackend.models.account.Account;
import uni.projects.remarketbackend.exceptions.exceptions.AuthenticationException;
import uni.projects.remarketbackend.exceptions.exceptions.NotFoundException;

import java.util.List;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 28.04.2025
 */

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AccountService accountService;

    public List<OrderDto> getOrders(HttpServletRequest request) throws AuthenticationException, NotFoundException {
        Account account = accountService.getAccount(request);
        if (account == null) {
            throw new AuthenticationException("User is not authenticated.");
        }

        List<OrderDto> orders = orderRepository.findAllByBuyer(account).stream()
                .map(OrderDto::valueFrom)
                .toList();

        if (orders.isEmpty()) {
            throw new NotFoundException("No orders found for the current user.");
        }

        return orders;
    }
}
