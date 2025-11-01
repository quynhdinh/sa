package esb.activators.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import esb.Order;

public class VisaPaymentActivator {
    
    public void pay(Order order) {
  		System.out.println("pay visa");
    }
}
