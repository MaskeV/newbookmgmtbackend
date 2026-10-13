package com.course.bookmgmtbackend.dto;

import java.time.LocalDate;

public class ExceptionResponseDto {

	private LocalDate localDate;
	private Integer statusCodeInteger;
	private String error;
	private String message;
	private String path;
	public LocalDate getLocalDate() {
		return localDate;
	}
	public void setLocalDate(LocalDate localDate) {
		this.localDate = localDate;
	}
	public Integer getStatusCodeInteger() {
		return statusCodeInteger;
	}
	public void setStatusCodeInteger(Integer statusCodeInteger) {
		this.statusCodeInteger = statusCodeInteger;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	
	
	
	
	
	
}
