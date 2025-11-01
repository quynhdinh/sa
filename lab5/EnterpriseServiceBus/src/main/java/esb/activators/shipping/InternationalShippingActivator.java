package esb.activators.shipping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import esb.Order;

public class InternationalShippingActivator {
    
	@Autowired
	RestTemplate restTemplate;

    public void ship(Order order) {
		System.out.println("shipping to 8084: "+ order.toString());
		restTemplate.postForLocation("http://localhost:8084/orders", order);
    }
}
