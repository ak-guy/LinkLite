package com.shortner.model;

public class UrlDTO {
	
	private String url;
	private String expiryDate;
	
	public UrlDTO() {}

	public UrlDTO(String url, String expiryDate) {
		super();
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

	@Override
	public String toString() {
		return "UrlDTO [url=" + url + ", expiryDate=" + expiryDate + "]";
	}
	
}
