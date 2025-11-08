package com.example.ticket.dto;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketRequest {
    private String bookingId;
    private String sagaId;
}
