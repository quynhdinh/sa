package com.example.consumer3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.kafka.support.KafkaHeaders;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.RetryableTopic;

@SpringBootApplication
@EnableKafka
public class Consumer3Application {
	public static void main(String[] args) {
		SpringApplication.run(Consumer3Application.class, args);
	}
}

@Component
class Listener {
	@RetryableTopic(attempts = "2")
	@KafkaListener(topics = "orders", groupId = "gid", properties = {"auto.offset.reset=earliest"})
	public void listen(String message, @Header(KafkaHeaders.OFFSET) Long offset, 
	@Header(KafkaHeaders.GROUP_ID) String groupId) {
		ObjectMapper objectMapper = new ObjectMapper();
		Order order = null;
		try {
			order = objectMapper.readValue(message, Order.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Received Order at offset " + offset + " and group id " + groupId + ": " + order);
		if (true) { // simulate error for all messages
			throw new RuntimeException("Invalid message received");
		}
	}
	@DltHandler
	public void handleDltMessage(String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String
topic, @Header(KafkaHeaders.OFFSET) long offset) {
		System.err.println("Received from DLT: " + message);
	}
}


record Order (
	String ordernumber,
	String customername,
	String customercountry,
	double amount,
	String status
) {}