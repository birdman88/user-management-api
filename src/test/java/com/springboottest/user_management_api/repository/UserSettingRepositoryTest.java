package com.springboottest.user_management_api.repository;

import com.springboottest.user_management_api.entity.User;
import com.springboottest.user_management_api.entity.UserSetting;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserSettingRepositoryTest {

    @Autowired
    private UserSettingRepository userSettingRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private UserSetting setting1;
    private UserSetting setting2;

    @BeforeEach
    void setUp() {
        // Create user
        user = User.builder()
                .ssn("0000000000001111")
                .firstName("John")
                .familyName("Doe")
                .birthDate(LocalDate.of(1990, 1, 1))
                .isActive(true)
                .build();
        user = userRepository.save(user);

        // Create settings
        setting1 = UserSetting.builder()
                .key("biometric_login")
                .value("false")
                .user(user)
                .build();

        setting2 = UserSetting.builder()
                .key("push_notification")
                .value("true")
                .user(user)
                .build();

        user.addUserSetting(setting1);
        setting1 = userSettingRepository.save(setting1);
        user.addUserSetting(setting2);
        setting2 = userSettingRepository.save(setting2);
    }

    @Test
    void findByUserId_shouldReturnAllSettingsForUser() {
        List<UserSetting> result = userSettingRepository.findByUserId(user.getId());

        assertThat(result).hasSize(2);
        assertThat(result).extracting(UserSetting::getKey)
                .containsExactlyInAnyOrder("biometric_login", "push_notification");
    }

    @Test
    void findByUserId_shouldReturnEmptyList_whenUserHasNoSettings() {
        User newUser = User.builder()
                .ssn("0000000000002222")
                .firstName("Jane")
                .familyName("Smith")
                .birthDate(LocalDate.of(1995, 5, 5))
                .isActive(true)
                .build();
        newUser = userRepository.save(newUser);
        List<UserSetting> result = userSettingRepository.findByUserId(newUser.getId());

        assertThat(result).isEmpty();
    }

    @Test
    void findByUserIdAndKey_shouldReturnSetting_whenExists() {
        Optional<UserSetting> result = userSettingRepository.findByUserIdAndKey(
                user.getId(), "biometric_login");

        assertThat(result).isPresent();
        assertThat(result.get().getValue()).isEqualTo("false");
    }

    @Test
    void findByUserIdAndKey_shouldReturnEmpty_whenKeyDoesNotExist() {
        Optional<UserSetting> result = userSettingRepository.findByUserIdAndKey(
                user.getId(), "email_notification");

        assertThat(result).isEmpty();
    }

    @Test
    void findByUserIdAndKey_shouldReturnEmpty_whenUserDoesNotExist() {
        Optional<UserSetting> result = userSettingRepository.findByUserIdAndKey(
                999L, "biometric_login");

        assertThat(result).isEmpty();
    }
}
