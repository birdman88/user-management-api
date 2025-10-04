package com.springboottest.user_management_api.util.validator.annotation.user_settings;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import com.springboottest.user_management_api.util.enums.UserSettingKey;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserSettingKeyValidatorImpl implements ConstraintValidator<UserSettingKeyValidator,
        List<Map<String, String>>> {

    @Override
    public boolean isValid(List<Map<String, String>> settings,
                           ConstraintValidatorContext context) {
        if (settings == null || settings.isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Settings cannot be empty")
                    .addConstraintViolation();
            return false;
        }

        // Convert list of maps to single map for validation
        Map<String, String> settingsMap = new HashMap<>();
        for (Map<String, String> setting : settings) {
            if (setting.size() != 1) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Each setting must have exactly one key-value pair")
                        .addConstraintViolation();
                return false;
            }
            settingsMap.putAll(setting);
        }

        List<String> errors = UserSettingKey.validateSettings(settingsMap);

        if (!errors.isEmpty()) {
            context.disableDefaultConstraintViolation();
            errors.forEach(error ->
                    context.buildConstraintViolationWithTemplate(error)
                            .addConstraintViolation()
            );
            return false;
        }

        return true;
    }
}
