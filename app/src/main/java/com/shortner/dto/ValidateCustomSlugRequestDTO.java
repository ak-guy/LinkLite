package com.shortner.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

public class ValidateCustomSlugRequestDTO {

	@NotEmpty(message = "Slug cannot be empty")
	@Pattern(regexp = "^[a-zA-Z0-9]{3,8}$", message = "Custom slug must have 3 to 8 alphanumeric characters")
	private String customSlug;

	public ValidateCustomSlugRequestDTO(String customSlug) {
		super();
		this.customSlug = customSlug;
	}

	public String getCustomSlug() {
		return customSlug;
	}

	public void setCustomSlug(String customSlug) {
		this.customSlug = customSlug;
	}

	@Override
	public String toString() {
		return "ValidateCustomSlugRequestDTO [customSlug=" + customSlug + "]";
	}

}
