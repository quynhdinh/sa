package com.example.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import com.example.dto.*;
@SpringBootApplication
public class PaymentApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentApplication.class, args);
	}
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

}
@RestController
@RequestMapping("/payment")
@Slf4j
class PaymentController {
	@Autowired
	private PaymentService svc;

	@PostMapping("/pay")
	public ResponseEntity<String> processPayment(@RequestBody PaymentRequest request) {
		log.info("Processing payment for bookingId: " + request.getBookingId());
		svc.pay(request);
		return ResponseEntity.ok("Payment successful");
		
	}
	@PostMapping("/refund")
	public ResponseEntity<String> processRefund(@RequestBody PaymentRequest request) {
		svc.refund(request);
		return ResponseEntity.ok("Refund successful");
	}
	@GetMapping("/status/{bookingId}")
	public ResponseEntity<String> getPaymentStatus(@PathVariable String bookingId) {
		// Implement status retrieval logic here
		return ResponseEntity.ok("Status for bookingId: " + bookingId);
	}
}
// collection: payments (bookingId, sagaId, amount, status)
@Service
@Slf4j
class PaymentService {
	@Autowired
	private PaymentRepository paymentRepository;
	@Autowired
	private RestTemplate restTemplate;
	// pay
	public void pay(PaymentRequest request) {
		if(request.getAmount() <= 0) {
			throw new IllegalArgumentException("Amount must be positive");
		}
		log.info("Calling to issue ticket for bookingId: " + request.getBookingId());
		// call to 8082 to issue a ticket
		restTemplate.postForObject("http://localhost:8082/tickets/issue", new TicketRequest(
			request.getBookingId(),
			request.getSagaId()
		), String.class);
		paymentRepository.save(new Payment(
			request.getBookingId(),
			request.getSagaId(),
			request.getAmount(),
			"PAID"
		));
	}
	// refund
	public void refund(PaymentRequest request) {
		paymentRepository.save(new Payment(
			request.getBookingId(),
			request.getSagaId(),
			request.getAmount(),
			"REFUNDED"
		));
	}
	// get status
	public String getPaymentStatus(String bookingId) {
		return paymentRepository.findById(bookingId)
			.map(Payment::getStatus)
			.orElse("NOT_FOUND");
	}
}
@Repository
interface PaymentRepository extends MongoRepository<Payment, String> {
}

@Document(collection = "payments")
@Data
@AllArgsConstructor
class Payment {
	@Id
	private String bookingId;
	private String sagaId;
	private double amount;
	private String status;
}