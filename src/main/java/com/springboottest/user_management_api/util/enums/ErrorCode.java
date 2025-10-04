package com.springboottest.user_management_api.util.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    RESOURCE_NOT_FOUND(30000, "Cannot find resource with id %s"),
    DUPLICATE_RESOURCE(30001, "Record with unique value %s already exists in the system"),
    INVALID_REQUEST(30002, "Invalid value for field %s, rejected value: %s"),
    SYSTEM_ERROR(80000, "System error, we're unable to process your request at the moment");

    private final int code;
    private final String messageTemplate;

    public String formatMessage(Object... args) {
        return String.format(messageTemplate, args);
    }
}
