package com.shortner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shortner.model.Url;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long>{
	
	public Url findbyShortUrl(String shortUrlLink);
	
	public boolean existsByShortUrl(String customSlug);
	
}
