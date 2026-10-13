package com.course.bookmgmtbackend.dto;

import com.course.bookmgmtbackend.entity.Role;
import com.course.bookmgmtbackend.entity.User;

public class AuthDtos {
	
	private AuthDtos() {
	}
 
	public record RegisterRequest(String username, String email, String password) {
	}
 
	public record LoginRequest(String email, String password) {
	}
 
	public record UserResponse(Long id, String username, String email, Role role) {
		public static UserResponse from(User user) {
			return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole());
		}
	}

}
