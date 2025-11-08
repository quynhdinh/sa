package com.example.ticket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import com.example.ticket.dto.TicketRequest;
import lombok.extern.slf4j.Slf4j;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@SpringBootApplication
public class TicketApplication {

	public static void main(String[] args) {
		SpringApplication.run(TicketApplication.class, args);
	}

}
@RestController
@RequestMapping("/tickets")
@Slf4j

class TicketController {
	@Autowired
	private TicketService svc;

	@PostMapping("/issue")
	public ResponseEntity<Ticket> issueTicket(@RequestBody TicketRequest request) {
		log.info("Issuing ticket for bookingId: " + request.getBookingId());
		Ticket ticket = svc.issueTicket(request);
		return ResponseEntity.ok(ticket);
	}

	@PostMapping("/cancel")
	public ResponseEntity<String> cancelTicket(@RequestBody TicketRequest request) {
		svc.cancelTicket(request);
		return ResponseEntity.ok("Cancellation successful");
	}
}

@Service
@Slf4j
class TicketService {
	@Autowired
	private TicketRepository ticketRepository;
	// issue
	public Ticket issueTicket(TicketRequest request) {
		log.info("Ticket issued for bookingId: " + request.getBookingId());
		return ticketRepository.save(new Ticket(
			request.getBookingId(),
			request.getSagaId(),
			"TK" + System.currentTimeMillis(),
			"ISSUED"
		));
	}
	// cancel
	public void cancelTicket(TicketRequest request) {
		// Implement cancellation logic here
		Ticket ticket = ticketRepository.findById(request.getBookingId()).orElse(null);
		if (ticket != null) {
			ticket.setStatus("CANCELLED");
			ticketRepository.save(ticket);
		}
	}
}
@Repository
interface TicketRepository extends MongoRepository<Ticket, String> {
}

@Document(collection = "tickets")
@Data
@AllArgsConstructor
@NoArgsConstructor
class Ticket {
	@Id
	private String bookingId;
	private String sagaId;
	private String ticketId;
	private String status;
}
// collection: tickets ((bookingId, sagaId, ticketId, status)
