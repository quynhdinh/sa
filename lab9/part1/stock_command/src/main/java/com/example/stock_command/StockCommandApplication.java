package com.example.stock_command;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Component;
import lombok.Data;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

import com.example.dto.StockChangeEventDTO;
import org.springframework.data.annotation.Id;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.config.TopicBuilder;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Configuration;
import lombok.extern.slf4j.Slf4j;
@SpringBootApplication
public class StockCommandApplication {

	public static void main(String[] args) {
		SpringApplication.run(StockCommandApplication.class, args);
	}
}

@Data
@Document(collection = "stocks_command")
class Stock {
	@Id
	private String productNumber;
	private int quantity;
}

@Repository
interface StockRepository extends MongoRepository<Stock, String> {
}
@RestController
class StockController {
	@Autowired
	private StockCommandService stockCommandService;

	@GetMapping("/stocks/{productNumber}")
	public Stock getStock(@PathVariable String productNumber) {
	    return stockCommandService.getStockByProductNumber(productNumber);
	}
	// curl -X POST -H "Content-Type: application/json" -d '{"productNumber":"P001","quantity":150}' http://localhost:8082/stocks
	@PostMapping("/stocks")
	public Stock addStock(@RequestBody Stock stock) {
	    return stockCommandService.addStock(stock);
	}
}
@Configuration
class KafkaTopicConfig {
	@Bean
	public NewTopic stockEventsTopic() {
		return TopicBuilder.name("stock-events")
				.partitions(1)
				.replicas(1)
				.build();
	}
}

@Service
@Slf4j
class StockCommandService {
	@Autowired
	private StockRepository stockRepository;
	@Autowired
	private KafkaTemplate<String, StockChangeEventDTO> kafkaTemplate;

	public Stock getStockByProductNumber(String productNumber) {
		return stockRepository.findById(productNumber).orElse(null);
	}
	public Stock addStock(Stock stock) {
		// if the stock for the product already exists, increase the quantity
		if (stock.getQuantity() <= 0) {
			throw new IllegalArgumentException("Quantity must be greater than zero");
		}
		Stock existingStock = stockRepository.findById(stock.getProductNumber()).orElse(null);
		if (existingStock != null) {
			existingStock.setQuantity(existingStock.getQuantity() + stock.getQuantity());
			stock = existingStock;
		}
		StockChangeEventDTO event = new StockChangeEventDTO(stock.getProductNumber(), stock.getQuantity());
		log.info("Sending stock change event: " + event);
		kafkaTemplate.send("stock-events", event);
		return stockRepository.save(stock);
	}
	public Stock updateStock(Stock stock) {
		return stockRepository.save(stock);
	}
	public void deleteStock(String productNumber) {
		stockRepository.deleteById(productNumber);
	}
}

// add some product here
// use CommandLineRunner or ApplicationRunner to add some initial products if needed
@Component
class DataLoader implements CommandLineRunner {
	@Autowired
	private StockRepository stockRepository;

	// Uncomment the following code to add initial products at startup
	@Override
	public void run(String... args) throws Exception {
		stockRepository.deleteAll();
		Stock stock1 = new Stock();
		stock1.setProductNumber("P001");
		stock1.setQuantity(100);
		stockRepository.save(stock1);

		Stock stock2 = new Stock();
		stock2.setProductNumber("P002");
		stock2.setQuantity(200);
		stockRepository.save(stock2);
	}
}