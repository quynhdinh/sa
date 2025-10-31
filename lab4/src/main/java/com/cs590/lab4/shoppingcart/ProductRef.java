package com.cs590.lab4.shoppingcart;

/**
 * Lightweight product reference used by the shopping cart for display only.
 * The authoritative product data (including price) lives in
 * com.cs590.lab4.product.Product (the product service / repository).
 */
public record ProductRef(String productNumber, String name) {
}
