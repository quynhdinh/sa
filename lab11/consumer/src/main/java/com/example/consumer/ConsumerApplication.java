package com.example.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
@EnableKafka
public class ConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConsumerApplication.class, args);
	}

}

@Component
class OrderListener {
	@KafkaListener(topics = "orders", groupId = "gid")
	// also print group id and offset
	public void listen(String message, 
	@Header(KafkaHeaders.OFFSET) Long offset, 
	@Header(KafkaHeaders.GROUP_ID) String groupId) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			Order order = objectMapper.readValue(message, Order.class);
			System.out.println("Received Order at offset " + offset + " and group id " + groupId + ": " + order);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
record Order (
	String ordernumber,
	String customername,
	String customercountry,
	double amount,
	String status
) {}