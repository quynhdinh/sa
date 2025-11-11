package com.example.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.Data;

import org.apache.kafka.clients.admin.NewTopic;

@SpringBootApplication
@AllArgsConstructor
public class ProducerApplication implements CommandLineRunner {

	private final OrderProducer orderProducer;
	private final OrderProducerBatch orderProducerBatch;
	public static void main(String[] args) {
		SpringApplication.run(ProducerApplication.class, args);
	}
	@Override
	public void run(String... args) throws Exception {
		// Order order1 = new Order("1001", "Alice", "USA", 250.75, "NEW");
		// Order order2 = new Order("1002", "Bob", "UK", 150.50, "NEW");
		// Order order3 = new Order("1003", "Charlie", "Canada", 300.00, "NEW");
		// Order order4 = new Order("1004", "Diana", "Australia", 450.25, "NEW");
		// Order order5 = new Order("1005", "Ethan", "Germany", 120.00, "NEW");
		// orderProducer.sendOrder(order1);
		// orderProducer.sendOrder(order2);
		// orderProducer.sendOrder(order3);
		// orderProducer.sendOrder(order4);
		// orderProducer.sendOrder(order5);
		orderProducerBatch.sendOrdersBatch();
	}

	@Bean
	public NewTopic ordersTopic() {
		return TopicBuilder.name("orders")
				.partitions(3)
				.replicas(3)
				.build();
	}
	// you might want 1 partition only, because if there are more partition than consumers,
	// some partitions will not be consumed
	@Bean
	public NewTopic ordersBatchTopic() {
		return TopicBuilder.name("orders_batch")
				.partitions(1)
				.replicas(1)
				.build();
	}
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class Order {
	private String ordernumber;
	private String customername;
	private String customercountry;
	private double amount;
	private String status;
}

@Component
@AllArgsConstructor
@Slf4j
class OrderProducer {
	private KafkaTemplate<String, String> kafkaTemplate;

	public void sendOrder(Order order) throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		String writeValueAsString = objectMapper.writeValueAsString(order);
		// send with different key to distribute across partitions
		// now not one consumer will get all messages
		kafkaTemplate.send("orders", order.getOrdernumber(), writeValueAsString);
	}
}

@Service
class OrderProducerBatch {
	@Autowired 
	private KafkaTemplate<String, String> kafkaTemplate;

	// 3 messages every 6 seconds
	public void sendOrdersBatch() throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		for(int i = 0; i < 20; i++){
			Order order = new Order("BATCH_ORDER_" + i, "Customer_" + i, "Country_" + i, i * 10.0, "NEW");
			String writeValueAsString = objectMapper.writeValueAsString(order);
			kafkaTemplate.send("orders_batch", "BATCH_ORDER_" + i, writeValueAsString);
			Thread.sleep(2000);
		}
	}
}