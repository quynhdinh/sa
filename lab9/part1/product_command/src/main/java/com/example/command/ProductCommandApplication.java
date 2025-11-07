package com.example.command;

import java.lang.annotation.Inherited;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Component;
import lombok.Data;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.config.TopicBuilder;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;
import com.example.dto.ProductChangeEventDTO;
import com.example.dto.ProductDeleteEventDTO;

@SpringBootApplication
public class ProductCommandApplication {
	public static void main(String[] args) {
		SpringApplication.run(ProductCommandApplication.class, args);
	}
}

@Data
@Document(collection = "products_command")
class Product {
	@Id
	private String productNumber;
	private String name;
	private int price;
}
@RestController
class ProductCommandController {
	@Autowired
	private ProductCommandService productCommandService;

	@GetMapping("/test")
	public String test() {
		return "Test endpoint";
	}
	// example curl command to test the add product endpoint:
	// curl -X POST -H "Content-Type: application/json" -d '{"productNumber":"P003","name":"Product 3","price":300}' http://localhost:8081/products
	@PostMapping("/products")
	public Product addProduct(@RequestBody Product product) {
		System.out.println("Add product endpoint called: " + product);
		return productCommandService.addProduct(product);
	}
	@PutMapping("/products")
	public Product updateProduct(@RequestBody Product product) {
	    return productCommandService.updateProduct(product);
	}
	// curl -X DELETE http://localhost:8081/products/P003
	@DeleteMapping("/products/{productNumber}")
	public void deleteProduct(@PathVariable String productNumber) {
	    productCommandService.deleteProduct(productNumber);
	}
}
@Repository
interface ProductRepository extends MongoRepository<Product, String> {
}
@Configuration
class KafkaConfig {
	// new topic called product-events
	@Bean
	public NewTopic productEventsTopic() {
		return TopicBuilder.name("product-events")
				.partitions(1)
				.replicas(1)
				.build();
	}
}
@Service
class ProductCommandService {
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private KafkaTemplate<String, ProductChangeEventDTO> kafkaTemplate;
	@Autowired
	private KafkaTemplate<String, ProductDeleteEventDTO> kafkaTemplate2;
	// test endpoint
	public Product addProduct(Product product) {
		// write a kafka message to notify ProductQueryService about the new product
		kafkaTemplate.send("product-events", new ProductChangeEventDTO(product.getProductNumber(), product.getName(), product.getPrice()));
		return productRepository.save(product);
	}
	// command to see all topics in kafka
	// bin/kafka-topics.sh --list --bootstrap-server localhost:9092

	public Product updateProduct(Product product) {
		return productRepository.save(product);
	}
	public void deleteProduct(String productNumber) {
		kafkaTemplate2.send("product-events", new ProductDeleteEventDTO(productNumber));
		productRepository.deleteById(productNumber);
	}
}

// add some product here
// use CommandLineRunner or ApplicationRunner to add some initial products if needed
@Component
class DataLoader implements CommandLineRunner {
	@Autowired
	private ProductRepository productRepository;

	// Uncomment the following code to add initial products at startup
	@Override
	public void run(String... args) throws Exception {
		productRepository.deleteAll();
		Product product1 = new Product();
		product1.setProductNumber("P001");
		product1.setName("Product 1");
		product1.setPrice(100);
		productRepository.save(product1);

		Product product2 = new Product();
		product2.setProductNumber("P002");
		product2.setName("Product 2");
		product2.setPrice(200);
		productRepository.save(product2);
	}
}