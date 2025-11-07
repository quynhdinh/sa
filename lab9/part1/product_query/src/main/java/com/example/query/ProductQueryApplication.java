package com.example.query;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import lombok.Data;
import java.util.List;
import java.util.Optional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.data.annotation.Id;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.KafkaHandler;
import com.example.dto.ProductChangeEventDTO;
import com.example.dto.ProductDeleteEventDTO;
import lombok.extern.slf4j.Slf4j;
import com.example.dto.StockChangeEventDTO;
@SpringBootApplication
public class ProductQueryApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductQueryApplication.class, args);
	}

}

@RestController
@RequestMapping("/products")
class ProductQueryController {
	@Autowired
	private ProductQueryService productQueryService;
	@GetMapping
	public List<Product> getAllProducts() {
		return productQueryService.getAllProducts();
	}
	@GetMapping("/{productNumber}")
	public Optional<Product> getProductByNumber(@PathVariable String productNumber) {
		return productQueryService.getProductByNumber(productNumber);
	}
}

@Service
class ProductQueryService {
	@Autowired
	private ProductRepository productRepository;
	public List<Product> getAllProducts() {
		return productRepository.findAll();
	}
	public Optional<Product> getProductByNumber(String productNumber) {
		return productRepository.findById(productNumber);
	}
}

@Document(collection = "products_query")
@Data
class Product {
	@Id
	private String productNumber;
	private String name;
	private int price;
	private int numberInStock;
}

@Repository
interface ProductRepository extends MongoRepository<Product, String> {
}

@Component
class DataLoader implements CommandLineRunner {
	@Autowired
	private ProductRepository productRepository;

	// a bit hacky way to pre-load some data
	@Override
	public void run(String... args) throws Exception {
		productRepository.deleteAll();
		Product product1 = new Product();
		product1.setProductNumber("P001");
		product1.setName("Product 1");
		product1.setPrice(100);
		product1.setNumberInStock(100);
		productRepository.save(product1);

		Product product2 = new Product();
		product2.setProductNumber("P002");
		product2.setName("Product 2");
		product2.setPrice(200);
		product2.setNumberInStock(200);
		productRepository.save(product2);
	}
}

@Component
@KafkaListener(topics = "product-events")
@Slf4j
class ProductChangeListener {
	@Autowired
	private ProductRepository productRepository;

	@KafkaHandler
	public void handleProductChangeEvent(ProductChangeEventDTO event) {
		log.info("Received product change event: " + event);
		Product existingProduct = productRepository.findById(event.getProductNumber()).orElse(null);
		Product product = new Product();
		if (existingProduct == null) { // new product
			product.setNumberInStock(0); // default stock to 0 if new product
		} else {
			product.setNumberInStock(existingProduct.getNumberInStock());
		}
		product.setProductNumber(event.getProductNumber());
		product.setName(event.getName());
		product.setPrice(event.getPrice());
		productRepository.save(product);
	}

	@KafkaHandler
	public void handleProductDeleteEvent(ProductDeleteEventDTO event) {
		log.info("Received product delete event for product number: " + event.getProductNumber());
		productRepository.deleteById(event.getProductNumber());
	}
}

@Component
@KafkaListener(topics = "stock-events")
@Slf4j
class ProductStockListener {
	@Autowired
	private ProductRepository productRepository;

	@KafkaHandler
	public void handleStock(StockChangeEventDTO event) {
		log.info("Received stock change event for product number: " + event.getProductNumber());
		Product existingProduct = productRepository.findById(event.getProductNumber()).orElse(null);
		if (existingProduct != null) {
			existingProduct.setNumberInStock(existingProduct.getNumberInStock() + event.getQuantity());
			productRepository.save(existingProduct);
		}
	}
}