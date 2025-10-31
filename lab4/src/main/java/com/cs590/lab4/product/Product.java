package com.cs590.lab4.product;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
@Document
@Data
@AllArgsConstructor
public class Product {
    @Id
    private String productNumber;
	private String name;
	private double price;
}