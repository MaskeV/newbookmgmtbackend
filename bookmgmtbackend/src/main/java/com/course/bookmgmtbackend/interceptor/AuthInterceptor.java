package com.course.bookmgmtbackend.interceptor;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerInterceptor;

import com.course.bookmgmtbackend.entity.Role;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class AuthInterceptor implements HandlerInterceptor {
 
	public static final String SESSION_USER_ID = "AUTH_USER_ID";
	public static final String SESSION_ROLE = "AUTH_ROLE";
 
	private static final AntPathMatcher MATCHER = new AntPathMatcher();
 
	/** method == null means "any HTTP method" */
	private record Rule(String method, String pattern, Set<Role> roles) {
	}
 
	// ---- PUBLIC paths: no login needed (like .permitAll()) ----
	private static final List<String> PUBLIC_PATHS = List.of(
			"/api/auth/**");
 
	// ---- ROLE rules (like .requestMatchers(...).hasRole(...)) ----
	private static final List<Rule> RULES = List.of(
			new Rule("GET", "/api/books/**", EnumSet.of(Role.USER, Role.ADMIN)),
			new Rule("POST", "/api/books/**", EnumSet.of(Role.ADMIN)),
			new Rule("PUT", "/api/books/**", EnumSet.of(Role.ADMIN)),
			new Rule("DELETE", "/api/books/**", EnumSet.of(Role.ADMIN)));
	// anything not matched above: any logged-in user (like .anyRequest().authenticated())
 
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
 
		String method = request.getMethod();
		if ("OPTIONS".equalsIgnoreCase(method)) { // CORS preflight
			return true;
		}
 
		String path = request.getRequestURI().substring(request.getContextPath().length());
 
		// 1. public?
		if (PUBLIC_PATHS.stream().anyMatch(p -> MATCHER.match(p, path))) {
			return true;
		}
 
		// 2. logged in?
		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute(SESSION_USER_ID) == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please log in first");
		}
 
		// 3. role allowed? (first matching rule wins)
		Role userRole = (Role) session.getAttribute(SESSION_ROLE);
		for (Rule rule : RULES) {
			boolean methodMatches = rule.method() == null || rule.method().equalsIgnoreCase(method);
			if (methodMatches && MATCHER.match(rule.pattern(), path)) {
				if (userRole == null || !rule.roles().contains(userRole)) {
					throw new ResponseStatusException(HttpStatus.FORBIDDEN,
							"You do not have permission to do this");
				}
				return true;
			}
		}
		return true; // no specific rule: logged-in is enough
	}
}
 
