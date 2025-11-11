package kafka;

import org.springframework.stereotype.Service;

@Service
public class FeeCalculatorService {
    
    public void logFeeRecord(FeeRecord feeRecord) {
        System.out.println("License Plate: " + feeRecord.getLicencePlate() +
                           ", Owner: " + feeRecord.getOwner() +
                           ", Speed: " + feeRecord.getSpeed() +
                           ", Fee: $" + feeRecord.getFee());
    }
}
