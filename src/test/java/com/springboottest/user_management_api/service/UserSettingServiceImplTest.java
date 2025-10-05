package com.springboottest.user_management_api.service;

import com.springboottest.user_management_api.dto.response.UserResponse;
import com.springboottest.user_management_api.entity.User;
import com.springboottest.user_management_api.entity.UserSetting;
import com.springboottest.user_management_api.exception.InvalidRequestException;
import com.springboottest.user_management_api.exception.ResourceNotFoundException;
import com.springboottest.user_management_api.repository.UserRepository;
import com.springboottest.user_management_api.repository.UserSettingRepository;
import com.springboottest.user_management_api.service.implementation.UserSettingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserSettingServiceImplTest {

    @Mock
    private UserSettingRepository userSettingRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserSettingServiceImpl userSettingService;

    private User user;
    private UserSetting existingSetting;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .ssn("0000000000001111")
                .firstName("John")
                .familyName("Doe")
                .birthDate(LocalDate.of(1990, 1, 1))
                .isActive(true)
                .userSettings(new ArrayList<>())
                .build();

        user.setCreatedTime(Instant.now());
        user.setUpdatedTime(Instant.now());
        user.setCreatedBy("SYSTEM");
        user.setUpdatedBy("SYSTEM");

        existingSetting = UserSetting.builder()
                .id(1L)
                .key("biometric_login")
                .value("false")
                .user(user)
                .build();

        user.getUserSettings().add(existingSetting);
    }

    @Test
    void createDefaultSettings_shouldCreateAllFiveDefaultSettings() {
        userSettingService.createDefaultSettings(user);

        verify(userSettingRepository, times(0)).save(any(UserSetting.class));
    }

    @Test
    void updateUserSettings_shouldUpdateExistingSetting() {
        List<Map<String, String>> settings = List.of(
                Map.of("biometric_login", "true")
        );

        when(userRepository.findActiveUserById(1L)).thenReturn(Optional.of(user));
        when(userSettingRepository.findByUserIdAndKey(1L, "biometric_login"))
                .thenReturn(Optional.of(existingSetting));
        when(userSettingRepository.save(any(UserSetting.class))).thenReturn(existingSetting);

        UserResponse response = userSettingService.updateUserSettings(1L, settings);

        verify(userSettingRepository).findByUserIdAndKey(1L, "biometric_login");
        verify(userSettingRepository).save(any(UserSetting.class));
        assertThat(response).isNotNull();
    }

    @Test
    void updateUserSettings_shouldCreateNewSetting_whenSettingDoesNotExist() {
        List<Map<String, String>> settings = List.of(
                Map.of("push_notification", "true")
        );
        when(userRepository.findActiveUserById(1L)).thenReturn(Optional.of(user));
        when(userSettingRepository.findByUserIdAndKey(1L, "push_notification"))
                .thenReturn(Optional.empty());
        when(userSettingRepository.save(any(UserSetting.class))).thenReturn(new UserSetting());

        UserResponse response = userSettingService.updateUserSettings(1L, settings);

        verify(userSettingRepository).save(any(UserSetting.class));
        assertThat(response).isNotNull();
    }

    @Test
    void updateUserSettings_shouldThrowException_whenUserNotFound() {
        List<Map<String, String>> settings = List.of(
                Map.of("biometric_login", "true")
        );
        when(userRepository.findActiveUserById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userSettingService.updateUserSettings(999L, settings))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateUserSettings_shouldThrowException_whenSettingKeyIsInvalid() {
        List<Map<String, String>> settings = List.of(
                Map.of("email_notification", "false")
        );
        when(userRepository.findActiveUserById(1L)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userSettingService.updateUserSettings(1L, settings))
                .isInstanceOf(InvalidRequestException.class);
    }

    @Test
    void updateUserSettings_shouldThrowException_whenSettingValueIsInvalid() {
        List<Map<String, String>> settings = List.of(
                Map.of("biometric_login", "maybe")
        );
        when(userRepository.findActiveUserById(1L)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userSettingService.updateUserSettings(1L, settings))
                .isInstanceOf(InvalidRequestException.class);
    }
}
