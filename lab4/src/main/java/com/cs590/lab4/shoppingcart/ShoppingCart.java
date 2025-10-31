package com.cs590.lab4.shoppingcart;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;

@Document
@Data
@AllArgsConstructor
public class ShoppingCart {
    @Id
    private String shoppingCartNumber;
    private List<OrderLine> orderLines;
    public void addOrderLine(OrderLine orderLine) {
        this.orderLines.add(orderLine);
    }
}
