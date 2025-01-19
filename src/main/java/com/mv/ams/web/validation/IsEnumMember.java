package com.mv.ams.web.validation;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IsEnumMember.IsEnumMemberValidator.class)
public @interface IsEnumMember {

    String message() default "The value provided is not one of: ";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    Class<? extends Enum<?>> enumClass();

    boolean appendValuesToMessage();

    class IsEnumMemberValidator implements ConstraintValidator<IsEnumMember, String> {

        private Set<String> enumValues;
        private boolean shouldAppendValues;

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            if(value == null) {
                return true;
            }

            boolean isValid = enumValues.contains(value);
            if(!isValid && shouldAppendValues) {
                context.disableDefaultConstraintViolation();
                String validValues = String.join(", ", enumValues);
                String newMessage = context.getDefaultConstraintMessageTemplate() + validValues;
                context.buildConstraintViolationWithTemplate(newMessage).addConstraintViolation();
            }

            return isValid;
        }

        @Override
        public void initialize(IsEnumMember constraintAnnotation) {
            enumValues = Stream.of(constraintAnnotation.enumClass().getEnumConstants())
                    .map(Enum::name)
                    .collect(Collectors.toSet());
            shouldAppendValues = constraintAnnotation.appendValuesToMessage();

            ConstraintValidator.super.initialize(constraintAnnotation);
        }
    }
}
