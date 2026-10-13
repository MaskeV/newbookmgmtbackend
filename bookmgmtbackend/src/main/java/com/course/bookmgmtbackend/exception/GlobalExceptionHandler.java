package com.course.bookmgmtbackend.exception;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
 
@RestControllerAdvice
public class GlobalExceptionHandler {
 
	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
 
	public record ErrorResponse(int status, String error, String message, LocalDateTime timestamp) {
	}
 
	@ExceptionHandler(BookNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleNotFound(BookNotFoundException ex) {
		return build(HttpStatus.NOT_FOUND, ex.getMessage());
	}
 
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException ex) {
		return build(HttpStatus.BAD_REQUEST, ex.getMessage());
	}
 
	// e.g. /api/books/abc where a number is expected
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
		return build(HttpStatus.BAD_REQUEST, "Invalid value for parameter '" + ex.getName() + "'");
	}
 
	// malformed / missing JSON body
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponse> handleUnreadable(HttpMessageNotReadableException ex) {
		return build(HttpStatus.BAD_REQUEST, "Malformed or missing request body");
	}
 
	@ExceptionHandler(BookServiceException.class)
	public ResponseEntity<ErrorResponse> handleService(BookServiceException ex) {
		log.error("Service error", ex);
		return build(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
	}
 
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleAny(Exception ex) {
		log.error("Unexpected error", ex);
		return build(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong. Please try again later.");
	}
 
	private ResponseEntity<ErrorResponse> build(HttpStatus status, String message) {
		return ResponseEntity.status(status)
				.body(new ErrorResponse(status.value(), status.getReasonPhrase(), message, LocalDateTime.now()));
	}
	
	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<ErrorResponse> handleStatus(ResponseStatusException ex) {
		return build(HttpStatus.valueOf(ex.getStatusCode().value()), ex.getReason());
	}
}
 
