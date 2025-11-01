package esb.activators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

public class PaymentActivator {
    
	@Autowired
	RestTemplate restTemplate;
    // sending payment requests to different payment services
    public void payVisa() {
        System.out.println("Processing Visa payment");
    }
    public void payMastercard() {
        System.out.println("Processing Mastercard payment");
    }
    public void payPaypal() {
        System.out.println("Processing PayPal payment");
    }
}
