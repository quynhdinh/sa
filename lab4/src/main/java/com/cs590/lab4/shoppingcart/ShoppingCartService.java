package com.cs590.lab4.shoppingcart;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.cs590.lab4.product.ProductService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Service
@AllArgsConstructor
@Slf4j
public class ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ProductService productService;

    // add product to shopping cart number 1
    public void addToShoppingCart(String productNumber, int quantity) {
        var product = productService.getProduct(productNumber);
        if(product.isEmpty()){
            log.error("Product with product number {} not found", productNumber);
            return;
        }
        log.info(product.get().toString());
        Product p = new Product(productNumber, product.get().getName(), product.get().getPrice());
        // Add the product to the shopping cart
        ShoppingCart shoppingCart = shoppingCartRepository.findById("1").orElseThrow();
        shoppingCart.addOrderLine(new OrderLine(p, quantity));
        shoppingCartRepository.save(shoppingCart);
    }
    public Optional<ShoppingCart> getShoppingCart(String shoppingCartNumber) {
        return shoppingCartRepository.findById(shoppingCartNumber);
    }
    // Update stored shopping-cart product entries when a product price changes
    public void updateProductPriceInCarts(String productNumber, double newPrice) {
        Iterable<ShoppingCart> carts = shoppingCartRepository.findAll();
        for (ShoppingCart cart : carts) {
            boolean changed = false;
            List<OrderLine> updated = new ArrayList<>();
            if (cart.getOrderLines() != null) {
                for (OrderLine ol : cart.getOrderLines()) {
                    Product p = ol.product();
                    if (p.productNumber().equals(productNumber)) {
                        Product newP = new Product(productNumber, p.name(), newPrice);
                        updated.add(new OrderLine(newP, ol.quantity()));
                        changed = true;
                    } else {
                        updated.add(ol);
                    }
                }
            }
            if (changed) {
                cart.setOrderLines(updated);
                shoppingCartRepository.save(cart);
            }
        }
    }
}
