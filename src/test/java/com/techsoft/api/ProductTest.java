package com.techsoft.api;

import com.techsoft.api.common.error.HttpResponseException;
import com.techsoft.api.domain.Product;
import com.techsoft.api.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductTest {

    private final ProductService productService;

    @Autowired
    public ProductTest(ProductService productService) {
        this.productService = productService;
    }

    @Test
    void throwToSaveProductWithoutDescription() {
        Product product = new Product();

        product.setName("Test");
        product.setPrice(10.0);

        assertThrows(HttpResponseException.class, () -> productService.saveDomain(product));
    }

    @Test
    void throwToSaveProductNegativeValue() {
        Product product = new Product();

        product.setName("Test");
        product.setDescription("Description test!");
        product.setPrice(-10.0);

        assertThrows(HttpResponseException.class, () -> productService.saveDomain(product));
    }
}
