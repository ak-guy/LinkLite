package com.shortner.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.shortner.validators.ExpirationDateValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = ExpirationDateValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidExpirationDate {
	
	String message() default "Expiration date must be greater than today's date";
	
	Class<?> [] groups() default {};
	
	Class<? extends Payload>[] payload() default {};
	
}
