package com.techsoft.api.controller;

import com.techsoft.api.authentication.domain.ApplicationUser;
import com.techsoft.api.authentication.service.AuthService;
import com.techsoft.api.form.ProductForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.techsoft.api.domain.Product;
import com.techsoft.api.service.ProductService;

/**
 * @author Matheus Brito
 */
@Slf4j
@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;
    private final AuthService authService;

    @Autowired
    public ProductController(ProductService productService, AuthService authService) {
        this.productService = productService;
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<Page<Product>> findAll(@PageableDefault Pageable pageable) {
        log.info("Request get all products!");

        ApplicationUser loggedUser = authService.getLoggedUser();

        log.info("Request made by {} with id {}", loggedUser.getUsername(), loggedUser.getId());

        Page<Product> productPage = productService.list(pageable);
        return ResponseEntity.ok(productPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> find(@PathVariable Long id) {
        log.info("Request get product of id #{}", id);

        Product product = productService.findById(id);
        return ResponseEntity.ok(product);
    }

    @PostMapping
    public ResponseEntity<Product> save(@RequestBody ProductForm productForm) {
        log.info("Request to save product ({})", productForm.getName());

        Product productSave = productService.saveDto(productForm);
        return ResponseEntity.ok(productSave);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("Request delete product of id #{}", id);

        productService.delete(id);
    }


}
