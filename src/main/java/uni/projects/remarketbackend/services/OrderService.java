package uni.projects.remarketbackend.services;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uni.projects.remarketbackend.dao.OrderRepository;
import uni.projects.remarketbackend.dto.order.OrderDto;
import uni.projects.remarketbackend.models.account.Account;

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

    public List<OrderDto> getOrders(HttpServletRequest request) {
        Account account = accountService.getAccount(request);
        return orderRepository.findAllByBuyer(account).stream()
                .map(OrderDto::valueFrom)
                .toList();
    }
}
