package com.course.bookmgmtbackend.configuration;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


import com.course.bookmgmtbackend.entity.Role;
import com.course.bookmgmtbackend.entity.User;

import com.course.bookmgmtbackend.repository.UserRepository;



@Configuration
public class GlobalConfiguration {
	
	

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
	    CorsConfiguration configuration = new CorsConfiguration();
	    configuration.setAllowedOrigins(List.of("http://localhost:5173", "http://127.0.0.1:5173","http://localhost:4200"));
	    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
	    configuration.setAllowedHeaders(List.of("*"));
	    configuration.setMaxAge(3600L);   // browser caches the preflight for 1 hour

	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    source.registerCorsConfiguration("/**", configuration);
	    return source;
	}
	
	@Bean
	CommandLineRunner seedAdmin(UserRepository repo, PasswordEncoder encoder) {
	    return args -> {
	        if (!repo.existsByEmail("admin@example.com")) {
	           User a = new User();
	            a.setUsername("admin");
	            a.setEmail("admin@example.com");
	            a.setPassword(encoder.encode("Admin@123"));   // change later
	            a.setRole(Role.ADMIN);
	            repo.save(a);
	        }
	    };
	}
	
	
}
