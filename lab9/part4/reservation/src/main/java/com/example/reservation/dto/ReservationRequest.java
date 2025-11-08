package com.example.reservation.dto;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationRequest {
    private String sagaId;    // correlation id for the saga
    private String bookingId; // business id for the booking
    private String flightId;
    private String seatNumber;
    private int amount;
}
