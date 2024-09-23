package com.shortner.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.shortner.dto.UrlDTO;
import com.shortner.dto.ErrorResponseDTO;
import com.shortner.dto.UrlResponseDTO;
import com.shortner.dto.ValidateCustomSlugRequestDTO;
import com.shortner.dto.ValidateCustomSlugResponseDTO;
import com.shortner.exceptions.CustomSlugExistsException;
import com.shortner.exceptions.UrlProcessingException;
import com.shortner.model.Url;
import com.shortner.services.UrlService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
public class UrlShortnerController {
	
	@Autowired
	private UrlService urlService;
	
	@PostMapping("/generate")
	@Async
	public CompletableFuture<ResponseEntity<?>> generateShortUrl(@Valid @RequestBody UrlDTO urlDto) {
		
		if (urlService.doesSlugexist(urlDto.getSlug())) {
			throw new CustomSlugExistsException("Slug already exists");
		}
		
		// getting the short url
		CompletableFuture<Url> urlToReturn = null;
		if (urlDto.getSlug() != null) {
			urlToReturn = urlService.createUrlWithCustomSlug(urlDto);
			
		}else {
			urlToReturn = urlService.createUrlwithRandomSlug(urlDto);
		}
		
		if (urlToReturn != null) {
			Url url = urlToReturn.join();
			UrlResponseDTO urlResponseDTO = new UrlResponseDTO();
			urlResponseDTO.setShortUrl(url.getShortLink());
			urlResponseDTO.setOriginalUrl(url.getOriginalUrl());
			urlResponseDTO.setExpirationDate(url.getExpiryDate());
			
			return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.OK).body(urlResponseDTO));
			
		} else {
			throw new UrlProcessingException("Error in processing request");
		}
	}
	
	@PostMapping("/validateSlug")
	public ResponseEntity<ValidateCustomSlugResponseDTO> validateCustomSlug(@Valid @RequestBody ValidateCustomSlugRequestDTO requestDTO) {
		String customSlug = requestDTO.getCustomSlug();
		
		if (urlService.doesSlugexist(customSlug)) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(new ValidateCustomSlugResponseDTO("Custom slug already exists"));
		}
		return ResponseEntity.status(HttpStatus.OK).body(new ValidateCustomSlugResponseDTO("Custom slug is available"));
	}
	
	
	@GetMapping("/{shortUrl}")
	@Async
	public CompletableFuture<ResponseEntity<?>> redirectToOriginalUrl(@PathVariable String shortUrl, HttpServletResponse response) throws IOException{
		
		if (StringUtils.isEmpty(shortUrl)) {
			ErrorResponseDTO urlErrorResponseDTO = new ErrorResponseDTO();
			urlErrorResponseDTO.setErrorCode("Empty Url");
			urlErrorResponseDTO.setStatus("400");
			return CompletableFuture.completedFuture(ResponseEntity.badRequest().body(urlErrorResponseDTO));
		}
		
		Url urlToReturnUrl = urlService.getEncodedUrl(shortUrl);
		
		if (urlToReturnUrl == null) {
			ErrorResponseDTO urlErrorResponseDTO = new ErrorResponseDTO();
			urlErrorResponseDTO.setErrorCode("Url does not exist or it might have expired");
			urlErrorResponseDTO.setStatus("400");
			return CompletableFuture.completedFuture(ResponseEntity.badRequest().body(urlErrorResponseDTO));
		}
		
		if (urlToReturnUrl.getExpiryDate().isBefore(LocalDateTime.now())) {
			urlService.deleteShortUrl(urlToReturnUrl);
			ErrorResponseDTO urlErrorResponseDTO = new ErrorResponseDTO();
			urlErrorResponseDTO.setErrorCode("Url Expired");
			urlErrorResponseDTO.setStatus("200");
			return CompletableFuture.completedFuture(ResponseEntity.ok().body(urlErrorResponseDTO));
		}
		
		response.sendRedirect(urlToReturnUrl.getOriginalUrl());
		return CompletableFuture.completedFuture(null);
		
	}
	
}
