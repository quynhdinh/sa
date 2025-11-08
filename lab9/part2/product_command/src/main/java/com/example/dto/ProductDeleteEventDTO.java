package com.example.dto;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
@NoArgsConstructor // No-arg constructor required for Jackson deserialization
public class ProductDeleteEventDTO {
    private String productNumber;
}
