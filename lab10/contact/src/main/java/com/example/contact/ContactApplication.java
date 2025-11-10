package com.example.contact;

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
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.config.http.SessionCreationPolicy;

@SpringBootApplication
public class ContactApplication {

	public static void main(String[] args) {
		SpringApplication.run(ContactApplication.class, args);
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
			System.out.println("Roles extracted: " + roles);
			// Convert to SimpleGrantedAuthority with ROLE_ prefix
			return roles.stream()
				.map(role -> new SimpleGrantedAuthority("ROLE_" + role))
				.collect(Collectors.toSet());
		});
		return converter;
	}
}

@RestController
class ContactController {

    private static final Map<String, Map<String, String>> MOCK = new HashMap<>();

    static {
        MOCK.put("1", Map.of("id", "1", "name", "Alice", "phone", "+1-555-0101"));
        MOCK.put("2", Map.of("id", "2", "name", "Bob", "phone", "+1-555-0102"));
        MOCK.put("3", Map.of("id", "3", "name", "Carol", "phone", "+1-555-0103"));
    }

    @GetMapping
    public Collection<Map<String, String>> all() {
        return MOCK.values();
    }

    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER')")
    @GetMapping("/phone")
    public ResponseEntity<String> getById() {
        System.out.println("Accessing contact phone data");
        return ResponseEntity.ok("641-555-1234");
    }
}
