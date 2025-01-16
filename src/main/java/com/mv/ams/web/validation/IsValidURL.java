package com.mv.ams.web.validation;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IsValidURL.IsValidURLValidator.class)
public @interface IsValidURL {

    String message() default "Address provided is not a valid URL";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class IsValidURLValidator implements ConstraintValidator<IsValidURL, String> {

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            if(value == null) {
                return true;
            }

            try {
                URL ignored = URI.create(value).toURL();
            } catch (MalformedURLException | IllegalArgumentException e) {
                return false;
            }

            return true;
        }
    }
}
