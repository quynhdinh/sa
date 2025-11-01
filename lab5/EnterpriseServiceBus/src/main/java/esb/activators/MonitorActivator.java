package esb.activators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import esb.Order;

public class MonitorActivator {
    
	@Autowired
	RestTemplate restTemplate;

    private String source;

    public void setSource(String source) {
        this.source = source;
    }

    public void log(Order order) {
        restTemplate.postForLocation("http://localhost:8089/ping", new MonitoringDTO(source, order));
    }
}
record MonitoringDTO(String source, Order order) {}