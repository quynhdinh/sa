package kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class OwnerService {
    
    @Value("${app.topic.ownertopic}")
    private String ownerTopic;
    @Autowired
    KafkaTemplate<String, FeeRecord> kafkaTemplate;
    @Autowired
    private FeeCalculatorService feeCalculatorService;
    @KafkaListener(topics = {"toofasttopic"})
    public void receive(@Payload SpeedRecord speedRecord,
                        @Headers MessageHeaders headers) {
        int speed = speedRecord.getSpeed();
        int fee = 0;
        if (speed > 90) {
            fee = 125;
        } else if (speed > 82) {
            fee = 80;
        } else if (speed > 77) {
            fee = 45;
        } else if (speed >= 72) {
            fee = 25;
        }
        String owner = "John Doe"; // Default owner
        FeeRecord feeRecord = new FeeRecord(speedRecord.getLicencePlate(), owner, speed, fee);
        feeCalculatorService.logFeeRecord(feeRecord);
        kafkaTemplate.send(ownerTopic, feeRecord);
    }
}
