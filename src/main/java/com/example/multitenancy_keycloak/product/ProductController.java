package com.example.multitenancy_keycloak.product;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductRepository repository;

    public ProductController(ProductRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public ProductEntity create(@RequestBody ProductEntity product) {
        return repository.save(product);
    }

    @GetMapping
    public List<ProductEntity> getAll() {
        return repository.findAll();
    }
}
