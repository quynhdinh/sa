package com.example.producer;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

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
	public static void main(String[] args) {
		SpringApplication.run(ProducerApplication.class, args);
	}
	@Override
	public void run(String... args) throws Exception {
		Order order1 = new Order("1001", "Alice", "USA", 250.75, "NEW");
		Order order2 = new Order("1002", "Bob", "UK", 150.50, "NEW");
		Order order3 = new Order("1003", "Charlie", "Canada", 300.00, "NEW");
		Order order4 = new Order("1004", "Diana", "Australia", 450.25, "NEW");
		Order order5 = new Order("1005", "Ethan", "Germany", 120.00, "NEW");
		orderProducer.sendOrder(order1);
		orderProducer.sendOrder(order2);
		orderProducer.sendOrder(order3);
		orderProducer.sendOrder(order4);
		orderProducer.sendOrder(order5);
	}

	@Bean
	public NewTopic ordersTopic() {
		return TopicBuilder.name("orders")
				.partitions(3)
				.replicas(3)
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
		kafkaTemplate.send("orders", writeValueAsString);
	}
}