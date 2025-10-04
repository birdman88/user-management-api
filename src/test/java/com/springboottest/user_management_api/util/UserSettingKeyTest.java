package com.springboottest.user_management_api.util;

import com.springboottest.user_management_api.util.enums.UserSettingKey;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class UserSettingKeyTest {

    @Test
    void fromKey_shouldReturnEnum_whenKeyExists() {
        Optional<UserSettingKey> result = UserSettingKey.fromKey("biometric_login");

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(UserSettingKey.BIOMETRIC_LOGIN);
    }

    @Test
    void fromKey_shouldReturnEmpty_whenKeyDoesNotExist() {
        Optional<UserSettingKey> result = UserSettingKey.fromKey("email_notification");

        assertThat(result).isEmpty();
    }

    @Test
    void isValidValue_shouldReturnTrue_whenValueMatchesPattern() {
        UserSettingKey key = UserSettingKey.BIOMETRIC_LOGIN;
        boolean result = key.isValidValue("true");

        assertThat(result).isTrue();
    }

    @Test
    void isValidValue_shouldReturnFalse_whenValueDoesNotMatchPattern() {
        UserSettingKey key = UserSettingKey.BIOMETRIC_LOGIN;
        boolean result = key.isValidValue("maybe");

        assertThat(result).isFalse();
    }

    @Test
    void isValidValue_shouldReturnFalse_whenValueIsNull() {
        UserSettingKey key = UserSettingKey.PUSH_NOTIFICATION;
        boolean result = key.isValidValue(null);

        assertThat(result).isFalse();
    }

    @Test
    void isValidValue_shouldReturnFalse_whenValueIsBlank() {
        UserSettingKey key = UserSettingKey.SMS_NOTIFICATION;
        boolean result = key.isValidValue("  ");

        assertThat(result).isFalse();
    }

    @Test
    void isValidValue_shouldValidateWidgetOrderPattern() {
        UserSettingKey key = UserSettingKey.WIDGET_ORDER;

        assertThat(key.isValidValue("1,2,3,4,5")).isTrue();
        assertThat(key.isValidValue("5,4,3,2,1")).isTrue();
        assertThat(key.isValidValue("1,2,3")).isFalse();
        assertThat(key.isValidValue("1,2,3,4,6")).isFalse();
        assertThat(key.isValidValue("1-2-3-4-5")).isFalse();
    }

    @Test
    void getDefaultSettings_shouldReturnAllDefaults() {
        Map<String, String> defaults = UserSettingKey.getDefaultSettings();

        assertThat(defaults).hasSize(5);
        assertThat(defaults.get("biometric_login")).isEqualTo("false");
        assertThat(defaults.get("push_notification")).isEqualTo("false");
        assertThat(defaults.get("sms_notification")).isEqualTo("false");
        assertThat(defaults.get("show_onboarding")).isEqualTo("false");
        assertThat(defaults.get("widget_order")).isEqualTo("1,2,3,4,5");
    }

    @Test
    void validateSettings_shouldReturnEmptyList_whenAllSettingsValid() {
        Map<String, String> settings = Map.of(
                "biometric_login", "true",
                "push_notification", "false",
                "widget_order", "5,4,3,2,1"
        );

        List<String> errors = UserSettingKey.validateSettings(settings);
        assertThat(errors).isEmpty();
    }

    @Test
    void validateSettings_shouldReturnErrors_whenKeyIsInvalid() {
        Map<String, String> settings = Map.of(
                "invalid_key", "value"
        );
        List<String> errors = UserSettingKey.validateSettings(settings);

        assertThat(errors).hasSize(1);
        assertThat(errors.get(0)).contains("Invalid setting key: invalid_key");
    }

    @Test
    void validateSettings_shouldReturnErrors_whenValueIsInvalid() {
        Map<String, String> settings = Map.of(
                "biometric_login", "maybe"
        );
        List<String> errors = UserSettingKey.validateSettings(settings);

        assertThat(errors).hasSize(1);
        assertThat(errors.get(0)).contains("Invalid value for setting biometric_login: maybe");
    }

    @Test
    void validateSettings_shouldReturnMultipleErrors_whenMultipleInvalid() {
        Map<String, String> settings = Map.of(
                "biometric_login", "maybe",
                "invalid_key", "value",
                "widget_order", "1,2,3"
        );
        List<String> errors = UserSettingKey.validateSettings(settings);

        assertThat(errors).hasSize(3);
    }

    @Test
    void validateSettings_shouldReturnError_whenSettingsIsNull_Or_IsEmpty() {
        Map<String, String> settings = Map.of();
        List<String> errors = UserSettingKey.validateSettings(null);
        List<String> error_empty = UserSettingKey.validateSettings(settings);

        assertThat(error_empty).hasSize(1);
        assertThat(error_empty.get(0)).isEqualTo("Settings cannot be empty");
        assertThat(errors).hasSize(1);
        assertThat(errors.get(0)).isEqualTo("Settings cannot be empty");
    }
}
