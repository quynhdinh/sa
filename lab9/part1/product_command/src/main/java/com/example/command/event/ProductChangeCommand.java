package com.example.command.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductChangeCommand {
    @NotBlank
    private String productNumber;

    @NotBlank
    private String name;

    @NotNull
    private Integer price;

    public ProductChangeCommand(String productNumber, String name, Integer price) {
        this.productNumber = productNumber;
        this.name = name;
        this.price = price;
    }
    /**
     * Optional expected version/sequence for optimistic concurrency.
     * If null the caller does not enforce a version check.
     */
    private Long expectedVersion;
}