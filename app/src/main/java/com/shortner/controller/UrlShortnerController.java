package com.shortner.controller;

import java.io.IOException;
import java.time.LocalDateTime;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.shortner.exceptions.CustomSlugExistsException;
import com.shortner.exceptions.UrlProcessingException;
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
		
		if (urlService.doesSlugexist(urlDto.getSlug())) {
			throw new CustomSlugExistsException("Slug already exists");
		}
		
		// getting the short url
		Url urlToReturn = null;
		if (urlDto.getSlug() != null) {
			urlToReturn = urlService.createUrlWithCustomSlug(urlDto);
			
		}else {
			urlToReturn = urlService.createUrlwithRandomSlug(urlDto);
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
	
}
