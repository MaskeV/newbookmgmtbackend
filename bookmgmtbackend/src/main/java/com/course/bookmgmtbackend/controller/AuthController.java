package com.course.bookmgmtbackend.controller;



import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.course.bookmgmtbackend.dto.AuthDtos.LoginRequest;
import com.course.bookmgmtbackend.dto.AuthDtos.RegisterRequest;
import com.course.bookmgmtbackend.dto.AuthDtos.UserResponse;
import com.course.bookmgmtbackend.entity.User;
import com.course.bookmgmtbackend.interceptor.AuthInterceptor;
import com.course.bookmgmtbackend.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;


/**
 * Simple version: no sessions, no interceptor.
 * Login just answers "do these credentials match a user?"
 
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/register")
	public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(UserResponse.from(authService.register(request)));
	}

	
	@PostMapping("/login")
	public ResponseEntity<UserResponse> login(@RequestBody LoginRequest request) {
		return ResponseEntity.ok(UserResponse.from(authService.login(request)));
	}
}

*/


@RequestMapping("/api/auth")
public class AuthController {
 
	private static final int SESSION_TIMEOUT_SECONDS = 30 * 60;
 
	private final AuthService authService;
 
	public AuthController(AuthService authService) {
		this.authService = authService;
	}
 
	@PostMapping("/register")
	public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(UserResponse.from(authService.register(request)));
	}
 
	// 200 + session cookie if credentials are correct, 401 if not
	@PostMapping("/login")
	public ResponseEntity<UserResponse> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
		User user = authService.login(request);
 
		// start a fresh session (prevents session fixation)
		HttpSession old = httpRequest.getSession(false);
		if (old != null) {
			old.invalidate();
		}
		HttpSession session = httpRequest.getSession(true);
		session.setMaxInactiveInterval(SESSION_TIMEOUT_SECONDS);
		session.setAttribute(AuthInterceptor.SESSION_USER_ID, user.getId());
		session.setAttribute(AuthInterceptor.SESSION_ROLE, user.getRole());
 
		return ResponseEntity.ok(UserResponse.from(user));
	}
 
	// 204 No Content
	@PostMapping("/logout")
	public ResponseEntity<Void> logout(HttpServletRequest httpRequest) {
		HttpSession session = httpRequest.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		return ResponseEntity.noContent().build();
	}
}