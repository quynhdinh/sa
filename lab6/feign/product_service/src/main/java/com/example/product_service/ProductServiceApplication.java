package com.example.product_service;

import java.lang.foreign.Linker.Option;
import java.util.Optional;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableFeignClients
public class ProductServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductServiceApplication.class, args);
	}
}


@RestController
@RequestMapping("/product")
@AllArgsConstructor
@Slf4j
class ProductController {
	private final StockClient stockClient;
	private final ProductService productService;

	@RequestMapping("/{id}")
	public Optional<Product> getProductById(@PathVariable String id) {
		log.info("Calling to StockService");
		Integer stock = stockClient.getStockByProductId(id);
		var product = productService.getProductById(id).map(p -> {
			p.setNumberOnStock(stock);
			return p;
		});
		return product;
	}
}

@Service
@AllArgsConstructor
class ProductService {
	private final ProductRepository productRepository;
	public Optional<Product> getProductById(String id) {
		return productRepository.findById(id);
	}
}
@Repository
interface ProductRepository extends JpaRepository<Product, String> {

}

@Data
@Entity
class Product {
	@Id
	private String productNumber;
	private String name;
	private Integer numberOnStock;
}
@FeignClient(name = "stock-service", url = "${stock.service.url}")
interface StockClient {
	@RequestMapping(method = RequestMethod.GET, value = "/stock/{productId}")
	Integer getStockByProductId(@PathVariable("productId") String productId);
}