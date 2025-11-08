package com.example.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor // No-arg constructor required for Jackson deserialization
public class ProductDeleteEventDTO {
    private String productNumber;
}
