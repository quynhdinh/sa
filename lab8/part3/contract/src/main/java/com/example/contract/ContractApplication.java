package com.example.contract;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class ContractApplication {

	public static void main(String[] args) {
		SpringApplication.run(ContractApplication.class, args);
	}

}

@RestController
class EvenOddController {
	@GetMapping("/validate")
	public String isNumberPrime(@RequestParam("number") Integer number) {
		return number % 2 == 0 ? "Even" : "Odd";
	}
}