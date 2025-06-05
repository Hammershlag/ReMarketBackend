package uni.projects.remarketbackend;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import uni.projects.remarketbackend.dao.OrderRepository;
import uni.projects.remarketbackend.dto.AccountDto;
import uni.projects.remarketbackend.dto.auth.JwtAuthResponse;
import uni.projects.remarketbackend.dto.auth.LoginDto;
import uni.projects.remarketbackend.dto.order.OrderDto;
import uni.projects.remarketbackend.models.account.Roles;
import uni.projects.remarketbackend.services.AccountService;
import uni.projects.remarketbackend.services.OrderService;
import uni.projects.remarketbackend.services.auth.AuthService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrderTests {

    @Autowired private OrderService orderService;
    @Autowired private AccountService accountService;
    @Autowired private AuthService authService;
    @Autowired private OrderRepository orderRepository;

    private JwtAuthResponse tokens;
    private AccountDto accountDto;

    @BeforeAll
    void setup() throws Exception {
        accountDto = new AccountDto("orderTestUser", "Password123!", "ordertest@example.com", Roles.USER.getRole());
        accountService.createUser(accountDto);

        LoginDto loginDto = new LoginDto(accountDto.getUsername(), accountDto.getPassword());
        tokens = authService.login(loginDto);
    }

    @AfterAll
    void cleanup() {
        orderRepository.deleteAll();
    }

    @Test
    @Order(1)
    void testGetOrders() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + tokens.getAccessToken());

        List<OrderDto> orders = orderService.getOrders(request);

        assertThat(orders).isNotNull();
        assertThat(orders).isInstanceOf(List.class);
    }
}