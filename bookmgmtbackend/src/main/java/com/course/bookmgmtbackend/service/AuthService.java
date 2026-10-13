package com.course.bookmgmtbackend.service;



import java.net.PasswordAuthentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.course.bookmgmtbackend.dto.AuthDtos.LoginRequest;
import com.course.bookmgmtbackend.dto.AuthDtos.RegisterRequest;
import com.course.bookmgmtbackend.entity.Role;
import com.course.bookmgmtbackend.entity.User;
import com.course.bookmgmtbackend.exception.BookServiceException;
import com.course.bookmgmtbackend.repository.UserRepository;

@Service
public class AuthService {
 
	private static final Logger log = LoggerFactory.getLogger(AuthService.class);
 
	// Only the standalone crypto module is used - not the Spring Security framework
	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	private final UserRepository userRepository;
 
	public AuthService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
 
	@Transactional
	public User register(RegisterRequest request) {
		if (request == null) {
			throw badRequest("Request body is required");
		}
		String username = trim(request.username());
		String email = trim(request.email()).toLowerCase();
		String password = request.password();
 
		if (username.length() < 3) {
			throw badRequest("Username must be at least 3 characters");
		}
		if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
			throw badRequest("A valid email is required");
		}
		if (password == null || password.length() < 8) {
			throw badRequest("Password must be at least 8 characters");
		}
 
		try {
			if (userRepository.existsByUsername(username)) {
				throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already taken");
			}
			if (userRepository.existsByEmail(email)) {
				throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
			}
 
			User user = new User();
			user.setUsername(username);
			user.setEmail(email);
			user.setPassword(passwordEncoder.encode(password)); // never store plain text
			user.setRole(Role.USER); // self-registration is always a normal user
 
			User saved = userRepository.save(user);
			log.info("User registered id={} username={}", saved.getId(), username);
			return saved;
		} catch (DataIntegrityViolationException e) {
			// two requests raced past the exists-checks; DB unique constraint caught it
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Username or email already in use");
		} catch (DataAccessException e) {
			log.error("Database error during registration", e);
			throw new BookServiceException("Could not register user", e);
		}
	}
 
	@Transactional(readOnly = true)
	public User login(LoginRequest request) {
		if (request == null || trim(request.email()).isEmpty() || request.password() == null) {
			throw badRequest("Email and password are required");
		}
		try {
			// emails are stored lowercase at registration, so normalize here too
			String email = trim(request.email()).toLowerCase();
			User user = userRepository.findByEmail(email).orElse(null);
 
			// Same message for "no such user" and "wrong password" so attackers can't tell which
			if (user == null || !passwordEncoder.matches(request.password(), user.getPassword())) {
				log.warn("Failed login attempt for email={}", email);
				throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
			}
			return user;
		} catch (DataAccessException e) {
			log.error("Database error during login", e);
			throw new BookServiceException("Could not log in", e);
		}
	}
 
	@Transactional(readOnly = true)
	public User getById(Long id) {
		try {
			return userRepository.findById(id)
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User no longer exists"));
		} catch (DataAccessException e) {
			log.error("Database error while fetching user id={}", id, e);
			throw new BookServiceException("Could not fetch user", e);
		}
	}
 
	private ResponseStatusException badRequest(String message) {
		return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
	}
 
	private String trim(String value) {
		return value == null ? "" : value.trim();
	}
}