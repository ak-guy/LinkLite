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
			return creationDate.plusSeconds(60);
		}
		return LocalDateTime.parse(expirationDate);
	}
	
	@Override
	public Url generateShorlUrl(UrlDTO urlDTO) {
		if (StringUtils.isNotEmpty(urlDTO.getUrl())) {
			String encodeUrl = encodeUrl(urlDTO.getUrl());
			Url url = new Url();
			url.setCreationDate(LocalDateTime.now());
			url.setOriginalUrl(urlDTO.getUrl());
			url.setShortLink(encodeUrl);
			url.setExpiryDate(getExpirationDateTime(urlDTO.getExpiryDate(), url.getCreationDate()));
			Url urlToReturn = persistShortUrl(url);
			if (urlToReturn != null) {
				return urlToReturn;
			}
			return null;
		}
		return null;
		
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
	
	public void deleteShortUrl(Url url) {
		urlRepository.delete(url);
	}
}
