
package esb.activators;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import esb.Order;

public class WarehouseActivator {

	@Autowired
	RestTemplate restTemplate;

	public Order checkStock(Order order) {
		// System.out.println("checking stock for order: " + order);
		restTemplate.postForLocation("http://localhost:8081/orders", order);
		return order;
	}
}
