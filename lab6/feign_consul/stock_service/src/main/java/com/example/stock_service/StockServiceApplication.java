package com.example.stock_service;

import java.util.Random;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class StockServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(StockServiceApplication.class, args);
	}

}
@RestController
@RequestMapping("/stock")
class StockController {
	
	@RequestMapping("/{id}")
	public Integer checkStock(@PathVariable String id) {
		return new Random().nextInt(101);
	}

}