package com.techsoft.api.service;

import com.techsoft.api.common.service.impl.AbstractCrudServiceImpl;
import com.techsoft.api.domain.Order;
import com.techsoft.api.form.OrderForm;
import com.techsoft.api.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService extends AbstractCrudServiceImpl<Order, Long, OrderRepository, OrderForm> {

    @Autowired
    OrderService(OrderRepository orderRepository) {
        super(orderRepository, Order.class);
    }
}
