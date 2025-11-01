package esb.activators.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import esb.Order;

public class PaypalPaymentActivator {
    
    public void pay(Order order) {
  		System.out.println("pay paypal");
    }
}
