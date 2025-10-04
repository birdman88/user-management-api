package com.springboottest.user_management_api.exception;

import lombok.Getter;

@Getter
public class DuplicateResourceException extends RuntimeException {

    private final String uniqueValue;

    public DuplicateResourceException(String uniqueValue) {
        super(String.format("Record with unique value %s already exists in the system", uniqueValue));
        this.uniqueValue = uniqueValue;
    }
}
