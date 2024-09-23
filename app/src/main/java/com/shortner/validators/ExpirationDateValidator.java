package com.shortner.validators;

import java.time.LocalDate;
import com.shortner.annotations.ValidExpirationDate;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ExpirationDateValidator implements ConstraintValidator<ValidExpirationDate, String>{
	@Override
	public void initialize(ValidExpirationDate constraintAnnotation) {
		
	}
	
	@Override
	public boolean isValid(String expirationDate, ConstraintValidatorContext context) {
		if (expirationDate == null) {
			return true; // Null values are handled by @NotEmpty annotation
		}
		
		LocalDate currentDate = LocalDate.now();
		LocalDate parsedExpirationDate = LocalDate.parse(expirationDate);
		
		return parsedExpirationDate.isAfter(currentDate);
	}
}