package com.springboottest.user_management_api.dto.request;

import com.springboottest.user_management_api.util.validator.annotation.user_settings.UserSettingKeyValidator;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserSettingsRequest {

    @NotEmpty(message = "Settings map is required")
    @UserSettingKeyValidator
    private List<Map<String, String>> settings;
}
