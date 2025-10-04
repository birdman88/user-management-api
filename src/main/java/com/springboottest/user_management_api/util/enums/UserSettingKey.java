package com.springboottest.user_management_api.util.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum UserSettingKey {

    BIOMETRIC_LOGIN("biometric_login", "false", "^(true|false)$"),
    PUSH_NOTIFICATION("push_notification", "false", "^(true|false)$"),
    SMS_NOTIFICATION("sms_notification", "false", "^(true|false)$"),
    SHOW_ONBOARDING("show_onboarding", "false", "^(true|false)$"),
    WIDGET_ORDER("widget_order", "1,2,3,4,5", "^[1-5](,[1-5]){4}$");

    private final String key;
    private final String defaultValue;
    private final String validationPattern;

    /**
     * Get enum by key string
     */
    public static Optional<UserSettingKey> fromKey(String key) {
        return Arrays.stream(values())
                .filter(settingKey -> settingKey.getKey().equals(key))
                .findFirst();
    }

    /**
    /**
     * Validate value for this setting key
     */
    public boolean isValidValue(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        return Pattern.matches(validationPattern, value);
    }

    /**
     * Get all default settings as a map
     */
    public static Map<String, String> getDefaultSettings() {
        return Arrays.stream(values())
                .collect(Collectors.toMap(
                        UserSettingKey::getKey,
                        UserSettingKey::getDefaultValue
                ));
    }

    /**
     * Validate all settings in a map
     */
    public static List<String> validateSettings(Map<String, String> settings) {
        List<String> errors = new ArrayList<>();

        if (settings == null || settings.isEmpty()) {
            errors.add("Settings cannot be empty");
            return errors;
        }

        settings.forEach((key, value) -> {
            Optional<UserSettingKey> settingKey = fromKey(key);

            if (settingKey.isEmpty()) {
                errors.add(String.format("Invalid setting key: %s", key));
            } else if (!settingKey.get().isValidValue(value)) {
                errors.add(String.format("Invalid value for setting %s: %s (expected pattern: %s)",
                        key, value, settingKey.get().getValidationPattern()));
            }
        });

        return errors;
    }

}
