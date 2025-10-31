package com.cs590.lab4;

import java.util.List;
import java.util.Optional;

import org.apache.catalina.connector.Response;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cs590.lab4.product.Product;
import com.cs590.lab4.product.ProductRepository;
import com.cs590.lab4.product.ProductService;
import com.cs590.lab4.shoppingcart.ShoppingCart;
import com.cs590.lab4.shoppingcart.ShoppingCartRepository;
import com.cs590.lab4.shoppingcart.ShoppingCartService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
public class Lab4Application {
	public static void main(String[] args) {
		SpringApplication.run(Lab4Application.class, args);
	}
}

@RestController
@RequestMapping("/api/products")
@AllArgsConstructor
class ProductController {
    private final ShoppingCartService shoppingCartService;
    private final ProductService productService;

    @GetMapping
    public Iterable<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    // 1. Add new product to the product component
    @PostMapping
    public void addProduct(Product product) {
        productService.addProduct(product);
    }

    // 2. Get the product from the product component using the productNumber and print to the
    @GetMapping("/{productNumber}")
    public Optional<Product> getProduct(@PathVariable String productNumber) {
        return productService.getProduct(productNumber);
    }

    // 3. Add this product to the shopping cart using the productNumber and quantity. Make sure the
    // shoppingcart get all product details from the product component.
    // example curl -X POST http://localhost:8080/api/products/cart -H "Content-Type: application/json" -d '{"productNumber":"P001","quantity":2}'
    @PostMapping("/cart")
    public ResponseEntity<String> addToShoppingCart(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        shoppingCartService.addToShoppingCart(shoppingCartDTO.productNumber(), shoppingCartDTO.quantity());
        return ResponseEntity.ok("Added " + shoppingCartDTO);
    }
    // 4. Get the shopping cart and print to the console.
    @GetMapping("/cart/{shoppingCartNumber}")
    public Optional<ShoppingCart> getShoppingCart(@PathVariable String shoppingCartNumber) {
        return shoppingCartService.getShoppingCart(shoppingCartNumber);
    }
    // 5. Change the price of this product in the product component. Make sure the price in the
    // shopping component also changes accordingly
    // example 
    // curl -X POST http://localhost:8080/api/products/changePrice -H "Content-Type: application/json" -d '{"productNumber":"P001","newPrice":15.0}'
    @PostMapping("/changePrice")
    public ResponseEntity<String> changePrice(@RequestBody ChangePriceDTO changePriceDTO) {
        String productNumber = changePriceDTO.productNumber();
        double newPrice = changePriceDTO.newPrice();
        Optional<Product> productOpt = productService.getProduct(productNumber);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            product.setPrice(newPrice);
            productService.addProduct(product);
            shoppingCartService.updateProductPriceInCarts(productNumber, newPrice);
            return ResponseEntity.ok("Price changed for product " + productNumber + " to " + newPrice);
        } else {
            return ResponseEntity.status(Response.SC_NOT_FOUND).body("Product not found");
        }
    }
    // 6. Get the shopping cart and print to the console. like 4
}
// productNumber and quantity
record ShoppingCartDTO(String productNumber, int quantity) {}

record ChangePriceDTO(String productNumber, double newPrice) {}

@Component
@Slf4j
@AllArgsConstructor
class DataLoader implements CommandLineRunner{

    private final ProductRepository productRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    @Override
    public void run(String... args) throws Exception {
        productRepository.save(new Product("P001", "Product 1", 10.0));
        productRepository.save(new Product("P002", "Product 2", 20.0));
        shoppingCartRepository.save(new ShoppingCart("1", List.of()));
        shoppingCartRepository.save(new ShoppingCart("2", List.of()));
        log.info("Products initialized");
    }
}