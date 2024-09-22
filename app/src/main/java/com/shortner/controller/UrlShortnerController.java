package com.shortner.controller;

import java.io.IOException;
import java.time.LocalDateTime;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.shortner.exceptions.CustomSlugExistsException;
import com.shortner.exceptions.UrlProcessingException;
import com.shortner.model.ErrorDetails;
import com.shortner.model.Url;
import com.shortner.model.UrlDTO;
import com.shortner.model.UrlErrorResponseDTO;
import com.shortner.model.UrlResponseDTO;
import com.shortner.services.UrlService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
public class UrlShortnerController {
	
	@Autowired
	private UrlService urlService;
	
	@PostMapping("/generate")
	public ResponseEntity<?> generateShortUrl(@Valid @RequestBody UrlDTO urlDto) {
		
		try {
			if (urlService.doesSlugexist(urlDto.getSlug())) {
				throw new CustomSlugExistsException("Slug already exists");
			}
			
			// getting the short url
			Url urlToReturn = null;
			if (urlDto.getSlug() != null) {
				urlToReturn = urlService.generateUrlWithCustomSlug(urlDto);
				
			}else {
				urlToReturn = urlService.generateShorlUrl(urlDto);
			}
			
			if (urlToReturn != null) {
				UrlResponseDTO urlResponseDTO = new UrlResponseDTO();
				urlResponseDTO.setShortUrl(urlToReturn.getShortLink());
				urlResponseDTO.setOriginalUrl(urlToReturn.getOriginalUrl());
				urlResponseDTO.setExpirationDate(urlToReturn.getExpiryDate());
				
				return ResponseEntity.status(HttpStatus.OK).body(urlResponseDTO);
				
			} else {
				throw new UrlProcessingException("Error in processing request");
			}
			
		} catch (CustomSlugExistsException e) {
			UrlErrorResponseDTO urlErrorResponseDTO = new UrlErrorResponseDTO();
			urlErrorResponseDTO.setErrorCode(e.getMessage());
			urlErrorResponseDTO.setStatus("409");
			
			return ResponseEntity.status(HttpStatus.CONFLICT).body(urlErrorResponseDTO);
			
		} catch (UrlProcessingException e) {
			UrlErrorResponseDTO urlErrorResponseDTO = new UrlErrorResponseDTO();
			urlErrorResponseDTO.setErrorCode(e.getMessage());
			urlErrorResponseDTO.setStatus("400");
			
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(urlErrorResponseDTO);
			
		} catch (Exception e) {
			UrlErrorResponseDTO urlErrorResponseDTO = new UrlErrorResponseDTO();
			urlErrorResponseDTO.setErrorCode("Unexpected Error occured: " + e.getMessage());
			urlErrorResponseDTO.setStatus("500");
			
			return new ResponseEntity<>(urlErrorResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/{shortUrl}")
	public ResponseEntity<?> redirectToOriginalUrl(@PathVariable String shortUrl, HttpServletResponse response) throws IOException{
		
		if (StringUtils.isEmpty(shortUrl)) {
			UrlErrorResponseDTO urlErrorResponseDTO = new UrlErrorResponseDTO();
			urlErrorResponseDTO.setErrorCode("Empty Url");
			urlErrorResponseDTO.setStatus("400");
			return new ResponseEntity<UrlErrorResponseDTO>(urlErrorResponseDTO, HttpStatus.OK);
		}
		
		Url urlToReturnUrl = urlService.getEncodedUrl(shortUrl);
		
		if (urlToReturnUrl == null) {
			UrlErrorResponseDTO urlErrorResponseDTO = new UrlErrorResponseDTO();
			urlErrorResponseDTO.setErrorCode("Url does not exist or it might have expired");
			urlErrorResponseDTO.setStatus("400");
			return new ResponseEntity<UrlErrorResponseDTO>(urlErrorResponseDTO, HttpStatus.OK);
		}
		
		if (urlToReturnUrl.getExpiryDate().isBefore(LocalDateTime.now())) {
			urlService.deleteShortUrl(urlToReturnUrl);
			UrlErrorResponseDTO urlErrorResponseDTO = new UrlErrorResponseDTO();
			urlErrorResponseDTO.setErrorCode("Url Expired");
			urlErrorResponseDTO.setStatus("200");
			return new ResponseEntity<UrlErrorResponseDTO>(urlErrorResponseDTO, HttpStatus.OK);
		}
		
		response.sendRedirect(urlToReturnUrl.getOriginalUrl());
		return null;
		
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<UrlErrorResponseDTO> handleValidationException(MethodArgumentNotValidException expObject) {
		String message = expObject.getBindingResult().getFieldError().getDefaultMessage();
		UrlErrorResponseDTO urlErrorResponseDTO = new UrlErrorResponseDTO();
		urlErrorResponseDTO.setErrorCode(message);
		urlErrorResponseDTO.setStatus("400");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(urlErrorResponseDTO);
	}
	
}
