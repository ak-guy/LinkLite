package com.shortner.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.shortner.dto.ErrorResponseDTO;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(CustomSlugExistsException.class)
	public ResponseEntity<ErrorResponseDTO> handleCustomSlugExistsException(CustomSlugExistsException e) {
		ErrorResponseDTO urlErrorResponseDTO = new ErrorResponseDTO();
		urlErrorResponseDTO.setErrorCode(e.getMessage());
		urlErrorResponseDTO.setStatus("409");
		
		return ResponseEntity.status(HttpStatus.CONFLICT).body(urlErrorResponseDTO);
	}
	
	@ExceptionHandler(UrlProcessingException.class)
	public ResponseEntity<ErrorResponseDTO> handleUrlProcessingException(UrlProcessingException e) {
		ErrorResponseDTO urlErrorResponseDTO = new ErrorResponseDTO();
		urlErrorResponseDTO.setErrorCode(e.getMessage());
		urlErrorResponseDTO.setStatus("400");
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(urlErrorResponseDTO);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponseDTO> handleException(Exception e) {
		ErrorResponseDTO urlErrorResponseDTO = new ErrorResponseDTO();
		urlErrorResponseDTO.setErrorCode("Unexpected Error occured: " + e.getMessage());
		urlErrorResponseDTO.setStatus("500");
		
//		return new ResponseEntity<>(urlErrorResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR); // same as below line
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(urlErrorResponseDTO);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponseDTO> handleValidationException(MethodArgumentNotValidException e) {
		String message = e.getBindingResult().getFieldError().getDefaultMessage();
		
		ErrorResponseDTO urlErrorResponseDTO = new ErrorResponseDTO();
		urlErrorResponseDTO.setErrorCode(message);
		urlErrorResponseDTO.setStatus("400");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(urlErrorResponseDTO);
	}

}
