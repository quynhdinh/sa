package com.example.command.event;
import java.lang.annotation.Inherited;
import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Document(collection = "product_events")
@Data
@NoArgsConstructor
public class Event {
    @Id
    private String id;
    private String aggregateId;
    private long sequence;
    private String type;
    private String payload;
    private Instant timestamp;
}
