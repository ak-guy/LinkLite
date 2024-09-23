package com.shortner.services;

import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import com.shortner.dto.UrlDTO;
import com.shortner.model.Url;

@Service
public interface UrlService {
	
	public CompletableFuture<Url> createUrlwithRandomSlug(UrlDTO urlDTO);
	
	public Url persistShortUrl(Url url);
	
	public Url getEncodedUrl(String url);
	
	public void deleteShortUrl(Url url);
	
	public boolean doesSlugexist(String slug);
	
	public CompletableFuture<Url> createUrlWithCustomSlug(UrlDTO urlDTO);
	
}
