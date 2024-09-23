package com.shortner.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.shortner.model.UrlErrorResponseDTO;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(CustomSlugExistsException.class)
	public ResponseEntity<UrlErrorResponseDTO> handleCustomSlugExistsException(CustomSlugExistsException e) {
		UrlErrorResponseDTO urlErrorResponseDTO = new UrlErrorResponseDTO();
		urlErrorResponseDTO.setErrorCode(e.getMessage());
		urlErrorResponseDTO.setStatus("409");
		
		return ResponseEntity.status(HttpStatus.CONFLICT).body(urlErrorResponseDTO);
	}
	
	@ExceptionHandler(UrlProcessingException.class)
	public ResponseEntity<UrlErrorResponseDTO> handleUrlProcessingException(UrlProcessingException e) {
		UrlErrorResponseDTO urlErrorResponseDTO = new UrlErrorResponseDTO();
		urlErrorResponseDTO.setErrorCode(e.getMessage());
		urlErrorResponseDTO.setStatus("400");
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(urlErrorResponseDTO);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<UrlErrorResponseDTO> handleException(Exception e) {
		UrlErrorResponseDTO urlErrorResponseDTO = new UrlErrorResponseDTO();
		urlErrorResponseDTO.setErrorCode("Unexpected Error occured: " + e.getMessage());
		urlErrorResponseDTO.setStatus("500");
		
//		return new ResponseEntity<>(urlErrorResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR); // same as below line
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(urlErrorResponseDTO);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<UrlErrorResponseDTO> handleValidationException(MethodArgumentNotValidException e) {
		String message = e.getBindingResult().getFieldError().getDefaultMessage();
		
		UrlErrorResponseDTO urlErrorResponseDTO = new UrlErrorResponseDTO();
		urlErrorResponseDTO.setErrorCode(message);
		urlErrorResponseDTO.setStatus("400");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(urlErrorResponseDTO);
	}

}
