package com.switchfully.order.api.orders;

import com.switchfully.order.api.orders.dtos.ItemGroupDto;
import com.switchfully.order.api.orders.dtos.OrderAfterCreationDto;
import com.switchfully.order.api.orders.dtos.OrderCreationDto;
import com.switchfully.order.api.orders.dtos.reports.ItemGroupReportDto;
import com.switchfully.order.api.orders.dtos.reports.OrdersReportDto;
import com.switchfully.order.domain.items.prices.Price;
import com.switchfully.order.domain.orders.Order;
import com.switchfully.order.domain.orders.orderitems.OrderItem;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.UUID;

import static com.switchfully.order.domain.orders.OrderTestBuilder.anOrder;
import static com.switchfully.order.domain.orders.orderitems.OrderItemTestBuilder.anOrderItem;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class OrderMapperTest {

    private OrderItemMapper orderItemMapperMock;
    private OrderMapper orderMapper;

    @Before
    public void setupService() {
        orderItemMapperMock = Mockito.mock(OrderItemMapper.class);
        orderMapper = new OrderMapper(orderItemMapperMock);
    }

    @Test
    public void toDomain() {
        OrderItem orderItem = anOrderItem().build();
        when(orderItemMapperMock.toDomain(any(ItemGroupDto.class))).thenReturn(orderItem);

        String customerId = UUID.randomUUID().toString();
        Order order = orderMapper.toDomain(new OrderCreationDto()
                .withCustomerId(customerId)
                .withItemGroups(new ItemGroupDto()
                                .withItemId(UUID.randomUUID().toString())
                                .withOrderedAmount(5),
                        new ItemGroupDto()
                                .withItemId(UUID.randomUUID().toString())
                                .withOrderedAmount(1)));

        assertThat(order).isNotNull();
        assertThat(order.getCustomerId().toString()).isEqualTo(customerId);
        assertThat(order.getOrderItems()).containsExactlyInAnyOrder(orderItem, orderItem);
    }

    @Test
    public void toOrderAfterCreationDto() {
        Order order = anOrder().withId(UUID.randomUUID()).build();

        OrderAfterCreationDto orderAfterCreationDto = orderMapper.toOrderAfterCreationDto(order);

        assertThat(orderAfterCreationDto).isNotNull();
        assertThat(orderAfterCreationDto.getOrderId()).isEqualTo(order.getId().toString());
        assertThat(orderAfterCreationDto.getTotalPrice()).isEqualTo(order.getTotalPrice().getAmountAsFloat());
    }

    @Test
    public void toOrdersReportDto() {
        OrderItem orderItem1 = anOrderItem().build();
        Order order1 = anOrder().withId(UUID.randomUUID()).withOrderItems(orderItem1).build();
        ItemGroupReportDto itemGroupReportDto = new ItemGroupReportDto();
        when(orderItemMapperMock.toItemGroupReportDto(orderItem1))
                .thenReturn(itemGroupReportDto);

        OrdersReportDto ordersReportDto = orderMapper.toOrdersReportDto(asList(order1));

        assertThat(ordersReportDto).isNotNull();
        assertThat(ordersReportDto.getTotalPriceOfAllOrders())
                .isEqualTo(orderItem1.getTotalPrice().getAmountAsFloat());
        assertThat(ordersReportDto.getOrders()).hasSize(1);
        assertThat(ordersReportDto.getOrders().get(0)).isNotNull();
        assertThat(ordersReportDto.getOrders().get(0).getOrderId()).isEqualTo(order1.getId().toString());
        assertThat(ordersReportDto.getOrders().get(0).getItemGroups()).containsExactly(itemGroupReportDto);
    }

    @Test
    public void toOrdersReportDto_multipleOrderItems() {
        OrderItem orderItem1 = anOrderItem().withItemPrice(Price.create(BigDecimal.valueOf(35))).build();
        OrderItem orderItem2 = anOrderItem().withItemPrice(Price.create(BigDecimal.valueOf(45))).build();
        OrderItem orderItem3 = anOrderItem().withItemPrice(Price.create(BigDecimal.valueOf(40))).build();
        Order order1 = anOrder().withId(UUID.randomUUID()).withOrderItems(orderItem1, orderItem2).build();
        Order order2 = anOrder().withId(UUID.randomUUID()).withOrderItems(orderItem3).build();
        ItemGroupReportDto itemGroupReportDto = new ItemGroupReportDto();
        when(orderItemMapperMock.toItemGroupReportDto(any(OrderItem.class))).thenReturn(eq(itemGroupReportDto));

        OrdersReportDto ordersReportDto = orderMapper.toOrdersReportDto(asList(order1, order2));

        assertThat(ordersReportDto).isNotNull();
        assertThat(ordersReportDto.getTotalPriceOfAllOrders())
                .isEqualTo(Price.add(orderItem3.getTotalPrice(),
                        Price.add(orderItem1.getTotalPrice(),orderItem2.getTotalPrice()))
                        .getAmountAsFloat());
        assertThat(ordersReportDto.getOrders()).hasSize(2);
    }

}