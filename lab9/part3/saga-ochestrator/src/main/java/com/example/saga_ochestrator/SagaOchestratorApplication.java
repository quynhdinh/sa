package com.example.saga_ochestrator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.context.annotation.Bean;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.UUID;

import com.example.saga_ochestrator.dto.*;
import lombok.Getter;
import lombok.Setter;
@SpringBootApplication
public class SagaOchestratorApplication {

	public static void main(String[] args) {
		SpringApplication.run(SagaOchestratorApplication.class, args);
	}
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}

// collection: sagas (sagaId, bookingId, currentStep, status, createdAt, updatedAt, data)
@Data
@Getter
@Setter
@Document(collection = "sagas")
class Saga {
	@Id
	private String sagaId;
	private String bookingId;
	private String currentStep;
	private String status;
	private String createdAt;
	private String updatedAt;
	private String data;
}

@RestController
@RequestMapping("/sagas")
@Slf4j
class SagaController {   
    @Autowired SagaService svc;

    @PostMapping("/book")
    public SagaResponse book(@RequestBody ReservationRequest req) {
        log.info("Starting saga for bookingId: " + req.getBookingId());
        return svc.startSaga(req);
    }
}
@Repository
interface SagaRepository extends MongoRepository<Saga, String> {
    Saga findBySagaId(String sagaId);
}

@Service
@Slf4j
class SagaService {
    @Autowired SagaRepository sagaRepo;

    private final RestTemplate rest = new RestTemplate();

    public SagaResponse startSaga(ReservationRequest req) {
        String sagaId = req.getSagaId() == null ? UUID.randomUUID().toString() : req.getSagaId();
        req.setSagaId(sagaId);
        Saga saga = new Saga();
        saga.setSagaId(sagaId);
        saga.setBookingId(req.getBookingId());
        saga.setCurrentStep("IN_PROGRESS");
        sagaRepo.save(saga);

        try {
            // Step 1: reserve seat
            // call to 8081/reserve, ReservationRequest
            rest.postForEntity("http://localhost:8080/reservations/reserve", req, Void.class);
            saga.setCurrentStep("RESERVED"); sagaRepo.save(saga);

            // Step 2: payment
            // call to 8082/pay, PaymentRequest
            PaymentRequest payReq = new PaymentRequest(
                req.getBookingId(),
                req.getSagaId(),
                100.0 // dummy amount
            );
            rest.postForEntity("http://localhost:8081/payment/pay", payReq, Void.class);
            saga.setCurrentStep("PAID"); sagaRepo.save(saga);

            // Step 3: issue ticket
            // call to 8083/issue, TicketRequest
            TicketRequest ticketReq = new TicketRequest(
                req.getBookingId(),
                req.getSagaId()
            );
            rest.postForEntity("http://localhost:8082/tickets/issue", ticketReq, Void.class);
            saga.setCurrentStep("TICKET_ISSUED"); saga.setStatus("COMPLETED"); sagaRepo.save(saga);

            return new SagaResponse(sagaId, "COMPLETED");
        } catch (Exception ex) {
            // compensate in reverse order based on saga.currentStep
            log.info(ex.toString());
            compensate(req, saga);
            saga.setStatus("FAILED"); sagaRepo.save(saga);
            return new SagaResponse(sagaId, "FAILED");
        }
    }

    private void compensate(ReservationRequest req, Saga saga) {
        TicketRequest ticketReq = new TicketRequest(
            req.getBookingId(),
            req.getSagaId()
        );
        if ("PAID".equals(saga.getCurrentStep())) {
            // refund then release
            PaymentRequest payReq = new PaymentRequest(
                req.getBookingId(),
                req.getSagaId(),
                100.0 // dummy amount
            );
            safePost("http://localhost:8082/refund", payReq);
            safePost("http://localhost:8081/release", ticketReq);
        } else if ("RESERVED".equals(saga.getCurrentStep())) {
            // release only
            safePost("http://localhost:8081/release", ticketReq);
        }
    }

    private void safePost(String url, Object req) {
        try { rest.postForEntity(url, req, Void.class); } catch(Exception e) { /* log and continue */ }
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class SagaResponse {
    private String sagaId;
    private String status;
}