package com.example.dto;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor // No-arg constructor required for Jackson deserialization
public class ProductChangeEventDTO {
	private String productNumber;
	private String name;
	private int price;

}