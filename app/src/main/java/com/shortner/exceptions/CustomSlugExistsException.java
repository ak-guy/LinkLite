package com.shortner.exceptions;

public class CustomSlugExistsException extends RuntimeException{
	public CustomSlugExistsException(String message) {
		super(message);
		
	}
}
