package com.springboottest.user_management_api.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class InvalidRequestException extends RuntimeException {

    private final List<String> errors;

    public InvalidRequestException(String message) {
        super(message);
        this.errors = List.of(message);
    }

    public InvalidRequestException(List<String> errors) {
        super("Invalid request");
        this.errors = errors;
    }
}
