package com.shortner.dto;

public class ValidateCustomSlugResponseDTO {
	
	private String message;

	public ValidateCustomSlugResponseDTO(String message) {
		super();
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "ValidateCustomSlugResponseDTO [message=" + message + "]";
	}
	
}
