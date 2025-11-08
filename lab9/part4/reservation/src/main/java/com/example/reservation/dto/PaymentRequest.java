package com.example.reservation.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class PaymentRequest {
    private String bookingId;
    private String sagaId;
    private double amount;
}
