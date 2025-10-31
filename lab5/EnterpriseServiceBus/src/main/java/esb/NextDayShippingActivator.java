package esb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

public class NextDayShippingActivator {
    
	@Autowired
	RestTemplate restTemplate;

    public void nextDayShip(Order order) {
		System.out.println("shipping to 8083: "+ order.toString());
		restTemplate.postForLocation("http://localhost:8083/orders", order);
    }
}
