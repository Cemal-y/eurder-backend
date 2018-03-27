package com.switchfully.order.domain.orders;

import com.switchfully.order.domain.Entity;
import com.switchfully.order.domain.items.prices.Price;
import com.switchfully.order.domain.orders.orderitems.OrderItem;
import com.switchfully.order.infrastructure.builder.Builder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Order extends Entity {

    private List<OrderItem> orderItems;
    private UUID customerId;

    public Order(OrderBuilder orderBuilder) {
        super(orderBuilder.id);
        orderItems = orderBuilder.orderItems;
        customerId = orderBuilder.customerId;
    }

    public List<OrderItem> getOrderItems() {
        return Collections.unmodifiableList(orderItems);
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public Price getTotalPrice() {
        return null;
    }

    @Override
    public String toString() {
        return "Order{"
                + "id=" + getId() +
                ", orderItems=" + orderItems +
                ", customerId=" + customerId +
                '}';
    }

    public static class OrderBuilder extends Builder<Order> {

        private UUID id;
        private List<OrderItem> orderItems;
        private UUID customerId;

        private OrderBuilder() {
        }

        public static OrderBuilder order() {
            return new OrderBuilder();
        }

        @Override
        public Order build() {
            return new Order(this);
        }

        public OrderBuilder withId(UUID id) {
            this.id = id;
            return this;
        }

        public OrderBuilder withOrderItems(OrderItem... orderItems) {
            this.orderItems = Arrays.asList(orderItems);
            return this;
        }

        public OrderBuilder withCustomerId(UUID customerId) {
            this.customerId = customerId;
            return this;
        }
    }

}