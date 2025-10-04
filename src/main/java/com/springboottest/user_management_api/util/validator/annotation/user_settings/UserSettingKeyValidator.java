package com.springboottest.user_management_api.util.validator.annotation.user_settings;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = UserSettingKeyValidatorImpl.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface UserSettingKeyValidator {

    String message() default "Invalid user settings";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
