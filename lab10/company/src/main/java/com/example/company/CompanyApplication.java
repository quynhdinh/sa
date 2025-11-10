package com.example.company;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.core.convert.converter.Converter;
import java.util.stream.Collectors;
import java.util.*;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@SpringBootApplication
public class CompanyApplication {

	public static void main(String[] args) {
		SpringApplication.run(CompanyApplication.class, args);
	}

}
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig {
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.httpBasic(Customizer.withDefaults());
		http
			.oauth2ResourceServer(oauth2 -> oauth2
				.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));
		http
			.sessionManagement(session -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		);
		return http.build();
	}
	@Bean
	public JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
		converter.setJwtGrantedAuthoritiesConverter(jwt -> {
			Set<String> roles = new HashSet<>();
			// Realm roles
			Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
			if (realmAccess != null && realmAccess.get("roles") instanceof Collection<?> realmRoles) {
				roles.addAll(realmRoles.stream().map(Object::toString).toList());
			}
			// Client roles (replace "myclient" with your client ID)
			Map<String, Object> resourceAccess = jwt.getClaimAsMap("resource_access");
			if (resourceAccess != null && resourceAccess.get("myclient") instanceof Map<?, ?> clientAccess) {
				Object clientRoles = ((Map<?, ?>) clientAccess).get("roles");
				if (clientRoles instanceof Collection<?> clientRolesList) {
					roles.addAll(clientRolesList.stream().map(Object::toString).toList());
				}
			}
			// System.out.println("Extracted roles: " + roles);
			// Convert to SimpleGrantedAuthority with ROLE_ prefix
			return roles.stream()
				.map(role -> new SimpleGrantedAuthority("ROLE_" + role))
				.collect(Collectors.toSet());
		});
		return converter;
	}

}
@Configuration
class CompanyConfig {
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}

@RestController
class CompanyController {

	private final RestTemplate restTemplate;

	public CompanyController(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	// public product data accessible to everyone
	@PreAuthorize("hasAnyRole('CUSTOMER')")
	@GetMapping("/productdata")
	public Map<String, Object> productData() {
		return Map.of(
			"company", "Company XYZ",
			"products", new String[]{"Payroll System", "Employee Portal", "Analytics"}
		);
	}


	// employee contact data - only EMPLOYEE and MANAGER can access (enforced by @PreAuthorize)
	@GetMapping("/phone")
	@PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER')")
	public ResponseEntity<String> employeeContact(@AuthenticationPrincipal Jwt jwt) {
		String url = "http://localhost:8082";
		String token = jwt.getTokenValue();
		restTemplate.getInterceptors().add((request, body, execution) -> {
			request.getHeaders().setBearerAuth(token);
			return execution.execute(request, body);
		});
		String response = restTemplate.getForObject(url + "/phone", String.class);
		return ResponseEntity.ok(response);
	}

	// salary data - only MANAGER can access
	@GetMapping("/salary")
	@PreAuthorize("hasRole('MANAGER')")
	public ResponseEntity<?> employeeSalary(@AuthenticationPrincipal Jwt jwt) {
		String url = "http://localhost:8083";
		String token = jwt.getTokenValue();
		restTemplate.getInterceptors().add((request, body, execution) -> {
			request.getHeaders().setBearerAuth(token);
			return execution.execute(request, body);
		});
		String response = restTemplate.getForObject(url + "/salary", String.class);
		return ResponseEntity.ok(response);
	}
}
