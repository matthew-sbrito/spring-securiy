package com.techsoft.api.form;

import com.techsoft.api.domain.Product;
import com.techsoft.api.enumeration.OrderStatus;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class OrderForm {

    private String address;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private Set<Product> products;
}
