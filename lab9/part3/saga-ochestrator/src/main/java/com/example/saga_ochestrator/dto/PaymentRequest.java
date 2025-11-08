package com.example.saga_ochestrator.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {
    private String bookingId;
    private String sagaId;
    private double amount;
}