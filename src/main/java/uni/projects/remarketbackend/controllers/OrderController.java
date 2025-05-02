package uni.projects.remarketbackend.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uni.projects.remarketbackend.dto.order.OrderDto;
import uni.projects.remarketbackend.exceptions.ExceptionDetails;
import uni.projects.remarketbackend.services.OrderService;

import java.util.List;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 28.04.2025
 */

@Tag(name = "Orders", description = "Endpoints for managing orders")
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @SneakyThrows
    @Operation(summary = "Get user orders", description = "Retrieve all orders for the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved orders"),
            @ApiResponse(responseCode = "404", description = "No orders found for the user",
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class))),
            @ApiResponse(responseCode = "406", description = "Authentication required",
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                         content = @Content(schema = @Schema(implementation = ExceptionDetails.class)))
    })
    @GetMapping
    @Transactional
    public ResponseEntity<List<OrderDto>> getOrders(HttpServletRequest request) {
        return ResponseEntity.ok(orderService.getOrders(request));
    }
}
