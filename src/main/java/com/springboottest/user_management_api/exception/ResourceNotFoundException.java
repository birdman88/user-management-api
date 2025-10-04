package com.springboottest.user_management_api.exception;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException{

    private final Long resourceId;

    public ResourceNotFoundException(Long resourceId) {
        super(String.format("Cannot find resource with id %d", resourceId));
        this.resourceId = resourceId;
    }

    public ResourceNotFoundException(String message) {
        super(message);
        this.resourceId = null;
    }
}
