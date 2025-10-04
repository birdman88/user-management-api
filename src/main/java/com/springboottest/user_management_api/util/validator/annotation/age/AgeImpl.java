package com.springboottest.user_management_api.util.validator.annotation.age;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class AgeImpl implements ConstraintValidator<Age, LocalDate> {

    private int max;

    @Override
    public void initialize(Age constraintAnnotation) {
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        LocalDate hundredYrs = LocalDate.now().minusYears(this.max);
        return value.isAfter(hundredYrs) || value.isEqual(hundredYrs);
    }
}
