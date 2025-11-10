package com.example.consumer3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.kafka.support.KafkaHeaders;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
@EnableKafka
public class Consumer3Application {
	public static void main(String[] args) {
		SpringApplication.run(Consumer3Application.class, args);
	}
}

@Component
class Listener {
	// also print group id and offset
	@KafkaListener(topics = "orders", groupId = "gid", properties = {"auto.offset.reset=earliest"})
	public void listen(String message, @Header(KafkaHeaders.OFFSET) Long offset, 
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