package com.example.reservation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.beans.BeanProperty;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import com.example.reservation.dto.ReservationRequest;
import lombok.Data;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.stereotype.Repository;
import com.example.reservation.dto.PaymentRequest;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
public class ReservationApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReservationApplication.class, args);
	}
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}

@RestController
@RequestMapping("/reservations")
@Slf4j
class ReservationController {
	@Autowired
	private ReservationService svc;
	// curl -X POST -H "Content-Type: application/json" -d '{"bookingId":"B001","sagaId":"S001","flightId":"F001","seatNumber":"12A"}' http://localhost:8080/reservations/reserve
	@PostMapping("/reserve")
	public ResponseEntity<Reservation> reserveSeat(@RequestBody ReservationRequest request) {
		log.info("Received reservation request: " + request);
		Reservation reservation = svc.reserveSeat(request);
		return ResponseEntity.ok(reservation);
	}
	@PostMapping("/release")
	public ResponseEntity<String> releaseSeat(@RequestBody ReservationRequest request) {
		svc.releaseSeat(request);
		return ResponseEntity.ok("Release successful");
	}
	@GetMapping("/status/{bookingId}")
	public ResponseEntity<String> getReservationStatus(@PathVariable String bookingId) {
		String status = svc.getReservationStatus(bookingId);
		return ResponseEntity.ok("Status for bookingId: " + bookingId + " is " + status);
	}
}


@Service
@Slf4j
class ReservationService {
	@Autowired
	private ReservationRepository repository;
	@Autowired
	private RestTemplate restTemplate;
	// reserve
	public Reservation reserveSeat(ReservationRequest request) {
		log.info("Reserving seat for bookingId: " + request.getBookingId());
		return repository.findById(request.getBookingId())
			.map(existingReservation -> {
				if ("RESERVED".equals(existingReservation.getStatus())) {
					return existingReservation; // idempotent case
				} else {
					// update status to RESERVED
					// call to 8081 with PaymentRequest
					pay(request);
					existingReservation.setStatus("RESERVED");
					return repository.save(existingReservation);
				}
			})
			.orElseGet(() -> {
				// create new reservation
				Reservation newReservation = new Reservation();
				newReservation.setBookingId(request.getBookingId());
				newReservation.setSagaId(request.getSagaId());
				newReservation.setFlightId(request.getFlightId());
				newReservation.setSeatNumber(request.getSeatNumber());
				newReservation.setStatus("RESERVED");
				pay(request);
				return repository.save(newReservation);
			});
	}
	private void pay(ReservationRequest request) {
		log.info("Calling Payment Service for bookingId: " + request.getBookingId());
		restTemplate.postForObject(
			"http://localhost:8081/payment/pay",
			new PaymentRequest(request.getBookingId(),
				request.getSagaId(),
				100.0), // fix 100.0 as dummy amount
			String.class
		);
	}
	// release
	// release: set status to RELEASED if present (idempotent).
	public Optional<Reservation> releaseSeat(ReservationRequest request) {
		return repository.findById(request.getBookingId())
			.map(existingReservation -> {
				if (!"RELEASED".equals(existingReservation.getStatus())) {
					existingReservation.setStatus("RELEASED");
					return repository.save(existingReservation);
				}
				return existingReservation;
			});
	}
	// get status
	public String getReservationStatus(String bookingId) {
		return repository.findById(bookingId)
			.map(Reservation::getStatus)
			.orElse("NOT_FOUND");
	}
}
interface ReservationRepository extends MongoRepository<Reservation, String> {
	// custom query methods can be defined here if needed
}
@Document(collection = "reservations")
@Data
class Reservation {
// collection: reservations (bookingId, sagaId, flightId, seatNumber, status)
	@Id
	private String bookingId;
	private String sagaId;
	private String flightId;
	private String seatNumber;
	private String status;
}
