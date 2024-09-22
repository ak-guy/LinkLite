package com.shortner.services;

import org.springframework.stereotype.Service;

import com.shortner.model.Url;
import com.shortner.model.UrlDTO;

@Service
public interface UrlService {
	
	public Url generateShorlUrl(UrlDTO urlDTO);
	
	public Url persistShortUrl(Url url);
	
	public Url getEncodedUrl(String url);
	
	public void deleteShortUrl(Url url);
	
	public boolean doesSlugexist(String slug);
	
	public Url generateUrlWithCustomSlug(UrlDTO urlDTO);
	
}
