package com.shortner.model;

import com.shortner.annotations.ValidExpirationDate;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

public class UrlDTO {
	
	@NotEmpty(message = "Url cannot be not empty")
	@Pattern(regexp = "^https?://.*", message = "Url must start with https:// or http://")
	private String url;
	
	@Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "expiry date must be yyyy-mm-dd")
	@ValidExpirationDate
	private String expiryDate;
	
	@Pattern(regexp = "^[a-zA-Z0-9]{3,8}", message = "Custom slug have 3 to 8 alphanumeric characters")
	private String slug;
	
	public UrlDTO() {}

	public UrlDTO(String url, String expiryDate) {
		this.url = url;
		this.expiryDate = expiryDate;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}
	
	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	@Override
	public String toString() {
		return "UrlDTO [url=" + url + ", expiryDate=" + expiryDate + ", slug=" + slug + "]";
	}
	
}
