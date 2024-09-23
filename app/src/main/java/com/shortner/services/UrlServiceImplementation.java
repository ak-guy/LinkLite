package com.shortner.services;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.google.common.hash.Hashing;
import com.shortner.dto.UrlDTO;
import com.shortner.model.Url;
import com.shortner.repository.UrlRepository;

@Component
public class UrlServiceImplementation implements UrlService{
	
	@Autowired
	private UrlRepository urlRepository;
	
	@Deprecated
	private String encodeUrl(String url) {
		String encodedUrl = "";
		LocalDateTime time = LocalDateTime.now();
		encodedUrl = Hashing.murmur3_32_fixed().hashString(url.concat(time.toString()), StandardCharsets.UTF_8).toString();
		return encodedUrl;
	}
	
	private LocalDateTime getExpirationDateTime(String expirationDate, LocalDateTime creationDate) {
		if (StringUtils.isBlank(expirationDate)) {
			// for manual testing
			return creationDate.plusSeconds(60);
		}
		LocalDate expiryDateObj = LocalDate.parse(expirationDate, DateTimeFormatter.ISO_DATE);
		LocalDateTime expiryDateTimeObj = expiryDateObj.atStartOfDay();
		return expiryDateTimeObj;
	}
	
	@Async
	@Override
	public CompletableFuture<Url> createUrlwithRandomSlug(UrlDTO urlDTO) {
		String randomSlug = generateRandomSlug();
		LocalDateTime createdDateTime = LocalDateTime.now();
		LocalDateTime expiryDateTime = getExpirationDateTime(urlDTO.getExpiryDate(), createdDateTime);
		
		Url urltoPersiUrl = new Url(urlDTO.getUrl(), randomSlug, createdDateTime, expiryDateTime);
		
		return CompletableFuture.completedFuture(persistShortUrl(urltoPersiUrl));
		
	}
	
	@Override
	public Url persistShortUrl(Url url) {
		Url urlToReturn = urlRepository.save(url);
		return urlToReturn;
	}
	
	@Override
	public Url getEncodedUrl(String url) {
		Url urlToReturn = urlRepository.findbyShortUrl(url);
		return urlToReturn;
	}
	
	@Override
	public void deleteShortUrl(Url url) {
		urlRepository.delete(url);
	}
	
	@Override
	public boolean doesSlugexist(String slug) {
		return urlRepository.existsByShortUrl(slug);
	}
	
	@Async
	@Override
	public CompletableFuture<Url> createUrlWithCustomSlug(UrlDTO urlDTO) {
		LocalDateTime creationDateTime = LocalDateTime.now();
		LocalDateTime expiryDateTime = getExpirationDateTime(urlDTO.getExpiryDate(), creationDateTime);
		
		Url toReturnUrl = new Url(urlDTO.getUrl(), urlDTO.getSlug(), creationDateTime, expiryDateTime);
		return CompletableFuture.completedFuture(persistShortUrl(toReturnUrl));
	}
	
	private String generateRandomSlug() {
		String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		String generatedString = "";
		
		boolean isUnique = false;
		while (!isUnique) {
			generatedString = getRandomString(characters, 7);
			if (!urlRepository.existsByShortUrl(generatedString)) {
				isUnique = true;
			}
		}
		
		return generatedString;
	}
	
	private String getRandomString(String chars, int length) {
		int n = chars.length();
		StringBuilder resString = new StringBuilder();
		Random random = new Random();
		
		for (int i=0; i<length; i++) {
			int index = random.nextInt(n);
			resString.append(chars.charAt(index));
		}
		
		return resString.toString();
	}
}
