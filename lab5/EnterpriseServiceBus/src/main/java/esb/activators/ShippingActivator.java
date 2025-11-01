package esb.activators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import esb.Order;

public class ShippingActivator {
    
	@Autowired
	RestTemplate restTemplate;

	public Order shipInternational(Order order) {
		System.out.println("shipping to 8084: "+ order.toString());
		restTemplate.postForLocation("http://localhost:8084/orders", order);
		return order;
	}

	public Order shipNextDay(Order order) {
		System.out.println("shipping to 8083: "+ order.toString());
		restTemplate.postForLocation("http://localhost:8083/orders", order);
		return order;
	}
	
	public Order shipNormal(Order order) {
		System.out.println("shipping to 8082: "+ order.toString());
		restTemplate.postForLocation("http://localhost:8082/orders", order);
		return order;
	}
}
