package com.revshop.mapper;

import com.revshop.dto.CheckoutRequest;
import com.revshop.entity.CartItem;
import com.revshop.entity.Order;
import com.revshop.entity.OrderItem;
import com.revshop.entity.OrderStatus;
import com.revshop.entity.User;

import java.math.BigDecimal;

public final class OrderMapper {

    private OrderMapper() {
    }

    public static Order toNewOrder(CheckoutRequest request, User buyer) {
        Order order = new Order();
        order.setBuyer(buyer);
        order.setStatus(OrderStatus.PLACED);
        order.setShippingAddress(request.getShippingAddress().trim());
        order.setBillingAddress(request.getBillingAddress().trim());
        return order;
    }

    public static OrderItem toOrderItem(Order order, CartItem cartItem, BigDecimal unitPrice) {
        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProduct(cartItem.getProduct());
        item.setSeller(cartItem.getProduct().getSeller());
        item.setQuantity(cartItem.getQuantity());
        item.setUnitPrice(unitPrice);
        return item;
    }
}
