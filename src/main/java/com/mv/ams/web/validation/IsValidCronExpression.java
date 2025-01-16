package com.mv.ams.web.validation;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import org.quartz.CronExpression;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IsValidCronExpression.IsValidCronExpressionValidator.class)
public @interface IsValidCronExpression {

    String message() default "Provided cron expression is not valid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class IsValidCronExpressionValidator implements ConstraintValidator<IsValidCronExpression, String> {

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            if(value == null) {
                return true;
            }

            return CronExpression.isValidExpression(value);
        }
    }
}
