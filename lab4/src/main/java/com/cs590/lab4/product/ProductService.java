package com.cs590.lab4.product;

import java.util.Optional;

import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    public Iterable<Product> getAllProducts() {
        return productRepository.findAll();
    }
    public void addProduct(Product product) {
        productRepository.save(product);
    }
    public Optional<Product> getProduct(String productNumber) {
        return productRepository.findById(productNumber);
    }
}