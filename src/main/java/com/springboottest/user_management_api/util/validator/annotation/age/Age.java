package com.springboottest.user_management_api.util.validator.annotation.age;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AgeImpl.class)
@Documented
public @interface Age {

    String message() default "Age is not valid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    int max();
}
