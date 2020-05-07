package com.switchfully.order.api.orders;

import com.switchfully.order.api.orders.dtos.OrderAfterCreationDto;
import com.switchfully.order.api.orders.dtos.OrderCreationDto;
import com.switchfully.order.api.orders.dtos.OrderDto;
import com.switchfully.order.api.orders.dtos.reports.OrdersReportDto;
import com.switchfully.order.service.orders.OrderService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/" + OrderController.RESOURCE_NAME)
@CrossOrigin(origins = "https://eurder-angular.herokuapp.com")
public class OrderController {

    public static final String RESOURCE_NAME = "orders";

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @Inject
    public OrderController(OrderService orderService, OrderMapper orderMapper) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<OrderDto> getAllOrders(@RequestParam(name = "shippableToday", required = false) boolean onlyIncludeShippableToday) {
        return orderService.getAllOrders(onlyIncludeShippableToday).stream()
                .map(order -> orderMapper.toDto(order))
                .collect(Collectors.toList());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public OrderAfterCreationDto createOrder(@RequestBody OrderCreationDto orderDto) {
        return orderMapper.toOrderAfterCreationDto(
                orderService.createOrder(
                        orderMapper.toDomain(orderDto)));
    }

    @PostMapping(path = "/{id}/reorder", produces = MediaType.APPLICATION_JSON_VALUE)
    public OrderAfterCreationDto reorderOrder(@PathVariable String id) {
        return orderMapper.toOrderAfterCreationDto(
                orderService.reorderOrder(UUID.fromString(id)));
    }

    @GetMapping(path = "/customers/{customerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public OrdersReportDto getOrdersForCustomerReport(@PathVariable String customerId) {
        return orderMapper.toOrdersReportDto(
                orderService.getOrdersForCustomer(UUID.fromString(customerId)));
    }

}
