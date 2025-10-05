package com.springboottest.user_management_api.service.implementation;

import com.springboottest.user_management_api.dto.response.UserResponse;
import com.springboottest.user_management_api.entity.User;
import com.springboottest.user_management_api.entity.UserSetting;
import com.springboottest.user_management_api.exception.InvalidRequestException;
import com.springboottest.user_management_api.exception.ResourceNotFoundException;
import com.springboottest.user_management_api.repository.UserRepository;
import com.springboottest.user_management_api.repository.UserSettingRepository;
import com.springboottest.user_management_api.service.interfaces.UserSettingService;
import com.springboottest.user_management_api.util.ResponseUtil;
import com.springboottest.user_management_api.util.enums.UserSettingKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserSettingServiceImpl implements UserSettingService {

    private final UserSettingRepository userSettingRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void createDefaultSettings(User user) {
        log.info("Creating default settings for user id: {}", user.getId());

        // get default settings from enum
        Map<String, String> defaultSettings = UserSettingKey.getDefaultSettings();

        // create user settings entity
        defaultSettings.forEach((key, value) -> {
            UserSetting setting = UserSetting.builder()
                    .key(key)
                    .value(value)
                    .user(user)
                    .build();
            user.addUserSetting(setting);
        });

        log.info("Created {} default settings for user id: {}", defaultSettings.size(), user.getId());
    }

    @Override
    @Transactional
    public UserResponse updateUserSettings(Long userId, List<Map<String, String>> settings) {
        log.info("Updating settings for user id: {}", userId);

        // check if user exists and is active
        User user = userRepository.findActiveUserById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(userId));

        log.info("about to convert to single map");
        // Convert list of maps to single map
        Map<String, String> settingsMap = new HashMap<>();
        for (Map<String, String> setting : settings) {
            settingsMap.putAll(setting);
        }

        log.info("about to validate all settings");
        // validate all settings first
        List<String> validationErrors = UserSettingKey.validateSettings(settingsMap);
        if (!validationErrors.isEmpty()) {
            log.error("Invalid settings for user id {}: {}", userId, validationErrors);
            throw new InvalidRequestException(validationErrors);
        }

        log.info("about to update");
        // update user setting
        settingsMap.forEach((key, value) -> {
            Optional<UserSetting> existingSetting =
                    userSettingRepository.findByUserIdAndKey(userId, key);

            log.info("check if present");
            if (existingSetting.isPresent()) {
                log.info("its present");
                // update existing setting
                UserSetting updatedSetting = existingSetting.get();
                updatedSetting.setValue(value);
                userSettingRepository.save(updatedSetting);
                log.debug("Updated setting {} to {} for user id: {}", key, value, userId);
            } else {
                log.info("not present");
                // create new setting if it doesn't exist
                UserSetting newSetting = UserSetting.builder()
                        .key(key)
                        .value(value)
                        .user(user)
                        .build();
                userSettingRepository.save(newSetting);
                log.debug("Created new setting {} with value {} for user id: {}", key, value, userId);
            }
        });

        log.info("Successfully updated {} settings for user id: {}", settingsMap.size(), userId);

        // fetch updated user settings
        User updatedUser = userRepository.findActiveUserById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(userId));

        return ResponseUtil.mapToUserResponse(updatedUser);
    }
}
