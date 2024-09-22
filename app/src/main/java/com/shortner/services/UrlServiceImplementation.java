package com.shortner.services;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.hash.Hashing;
import com.shortner.model.Url;
import com.shortner.model.UrlDTO;
import com.shortner.repository.UrlRepository;

@Component
public class UrlServiceImplementation implements UrlService{
	
	@Autowired
	private UrlRepository urlRepository;
	
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
		return LocalDateTime.parse(expirationDate);
	}
	
	@Override
	public Url generateShorlUrl(UrlDTO urlDTO) {
		String encodeUrl = encodeUrl(urlDTO.getUrl());
		LocalDateTime createdDateTime = LocalDateTime.now();
		LocalDateTime expiryDateTime = getExpirationDateTime(urlDTO.getExpiryDate(), createdDateTime);
		
		Url urltoPersiUrl = new Url(urlDTO.getUrl(), encodeUrl, createdDateTime, expiryDateTime);
		
		return persistShortUrl(urltoPersiUrl);
		
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
	
	@Override
	public Url generateUrlWithCustomSlug(UrlDTO urlDTO) {
		LocalDateTime creationDateTime = LocalDateTime.now();
		LocalDateTime expiryDateTime = getExpirationDateTime(urlDTO.getExpiryDate(), creationDateTime);
		
		Url toReturnUrl = new Url(urlDTO.getUrl(), urlDTO.getSlug(), creationDateTime, expiryDateTime);
		return toReturnUrl;
	}
	
}
