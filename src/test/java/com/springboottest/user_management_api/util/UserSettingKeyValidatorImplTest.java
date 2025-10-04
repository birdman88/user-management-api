package com.springboottest.user_management_api.util;

import com.springboottest.user_management_api.util.validator.annotation.user_settings.UserSettingKeyValidatorImpl;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserSettingKeyValidatorImplTest {

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder builder;

    private UserSettingKeyValidatorImpl userSettingKeyValidator;

    @BeforeEach
    void setUp() {
        userSettingKeyValidator = new UserSettingKeyValidatorImpl();
        lenient().when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
    }

    @Test
    void isValid_shouldReturnTrue_whenAllSettingsAreValid() {
        List<Map<String, String>> settings = List.of(
                Map.of("biometric_login", "true"),
                Map.of("push_notification", "false"),
                Map.of("widget_order", "5,4,3,2,1")
        );
        boolean result = userSettingKeyValidator.isValid(settings, context);

        assertThat(result).isTrue();
        verifyNoInteractions(context);
    }

    @Test
    void isValid_shouldReturnFalse_whenSettingsIsEmpty() {
        List<Map<String, String>> settings = List.of();
        boolean result = userSettingKeyValidator.isValid(settings, context);

        assertThat(result).isFalse();
        verify(context).disableDefaultConstraintViolation();
    }

    @Test
    void isValid_shouldReturnFalse_whenSettingKeyIsInvalid() {
        List<Map<String, String>> settings = List.of(
                Map.of("email_notification", "false")
        );
        boolean result = userSettingKeyValidator.isValid(settings, context);

        assertThat(result).isFalse();
    }

    @Test
    void isValid_shouldReturnFalse_whenSettingValueIsInvalid() {
        List<Map<String, String>> settings = List.of(
                Map.of("biometric_login", "maybe")
        );
        boolean result = userSettingKeyValidator.isValid(settings, context);

        assertThat(result).isFalse();
    }

    @Test
    void isValid_shouldReturnFalse_whenMapHasMultipleEntries() {
        List<Map<String, String>> settings = List.of(
                Map.of("biometric_login", "true", "push_notification", "false")
        );
        boolean result = userSettingKeyValidator.isValid(settings, context);

        assertThat(result).isFalse();
    }
}
