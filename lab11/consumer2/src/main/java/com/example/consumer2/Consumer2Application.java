package com.example.consumer2;

import java.util.function.BiConsumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.util.backoff.FixedBackOff;
import org.springframework.kafka.support.KafkaHeaders;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
@EnableKafka
public class Consumer2Application {
	public static void main(String[] args) {
		SpringApplication.run(Consumer2Application.class, args);
	}
}

@Component
class Listener {
	// also print group id and offset
	@KafkaListener(topics = "orders", groupId = "gid", properties = {"auto.offset.reset=earliest"})
	public void listen(String message, 
						@Header(KafkaHeaders.OFFSET) Long offset, 
						@Header(KafkaHeaders.GROUP_ID) String groupId) {
		ObjectMapper objectMapper = new ObjectMapper();
		Order order = null;
		try {
			order = objectMapper.readValue(message, Order.class); // definitely readable
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Received Order at offset " + offset + " and group id " + groupId + ": " + order);
		if(true){ // simulate error for all messages
			throw new RuntimeException("Simulated processing error for Germany and USA orders");
		}
	}
	@KafkaListener(topics = "orders.DLT", groupId = "gid")
	public void handleDltMessage(String message) {
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

@Configuration
class KafkaConfig {
	@Bean
	public DefaultErrorHandler errorHandler(KafkaTemplate<Object, Object> kafkaTemplate) {
		DeadLetterPublishingRecoverer recoverer = new
		DeadLetterPublishingRecoverer(kafkaTemplate,
		(record, exception) -> {
			return new TopicPartition(record.topic() + ".DLT", record.partition());
		});
		// Retry twice with 1 second interval, then send to DLT
		FixedBackOff backOff = new FixedBackOff(1000L, 2L);
		DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, backOff);
		return errorHandler;
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, String> 
			kafkaListenerContainerFactory(
				ConsumerFactory<String, String> consumerFactory,
				DefaultErrorHandler errorHandler) {
		ConcurrentKafkaListenerContainerFactory<String, String> factory =
		new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory);
		factory.setCommonErrorHandler(errorHandler);
		return factory;
	}
}