package products;

import org.springframework.beans.factory.annotation.Autowired;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Service
public class ProductService {
	@Autowired
	StockFeignClient stockClient;

	private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

	@CircuitBreaker(name = "demoCircuitBreaker", fallbackMethod = "getProductFallback")
	public Product getProduct(String productNumber) {
		int stock = stockClient.getStock(productNumber);
		Product product = new Product("IPhone 11", productNumber);
		product.setNumberOnStock(stock);
		return product;
	}

	private Product getProductFallback(String productNumber, Throwable throwable) {
		logger.warn("getProductFallback invoked for productNumber={} cause={}", productNumber,
				throwable == null ? "-" : throwable.toString());
		return null;
	}

	// Use service id matching the stock service's spring.application.name
	// Remove the hard-coded URL so Feign uses the discovery client + load balancer
	@FeignClient(name = "stock-service")
	interface StockFeignClient {
		@RequestMapping("/stock/{productNumber}")
		public Integer getStock(@PathVariable("productNumber") String productNumber);
	}

}
