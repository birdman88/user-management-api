package com.springboottest.user_management_api.repository;

import com.springboottest.user_management_api.entity.User;
import com.springboottest.user_management_api.entity.UserSetting;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSettingRepository userSettingRepository;

    private User activeUser;
    private User deletedUser;

    @BeforeEach
    void setUp() {
        activeUser = User.builder()
                .ssn("0000000000001111")
                .firstName("John")
                .familyName("Doe")
                .birthDate(LocalDate.of(1990, 1, 1))
                .isActive(true)
                .build();

        deletedUser = User.builder()
                .ssn("0000000000002222")
                .firstName("Jane")
                .familyName("Smith")
                .birthDate(LocalDate.of(1995, 5, 5))
                .isActive(false)
                .deletedTime(Instant.now())
                .build();

        userRepository.save(activeUser);
        userRepository.save(deletedUser);

        UserSetting setting = UserSetting.builder()
                .key("biometric_login")
                .value("false")
                .user(activeUser)
                .build();

        activeUser.addUserSetting(setting);
        userSettingRepository.save(setting);
    }

    @Test
    void findAllActiveUsers_shouldReturnOnlyActiveUsers() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<User> result = userRepository.findAllActiveUsers(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(activeUser.getId());
        assertThat(result.getContent().get(0).getIsActive()).isTrue();
    }

    @Test
    void findAllActiveUsers_shouldRespectPagination() {
        for (int i = 0; i < 5; i++) {
            User user = User.builder()
                    .ssn(String.format("000000000000%04d", 3000 + i))
                    .firstName("User" + i)
                    .familyName("Test")
                    .birthDate(LocalDate.of(1990, 1, 1))
                    .isActive(true)
                    .build();
            userRepository.save(user);
        }
        Pageable pageable = PageRequest.of(0, 3);
        Page<User> result = userRepository.findAllActiveUsers(pageable);

        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getTotalElements()).isEqualTo(6);
    }

    @Test
    void findActiveUserById_shouldReturnUser_whenUserIsActive() {
        Optional<User> result = userRepository.findActiveUserById(activeUser.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(activeUser.getId());
        assertThat(result.get().getUserSettings()).hasSize(1);
    }

    @Test
    void findActiveUserById_shouldReturnEmpty_whenUserIsDeleted() {
        Optional<User> result = userRepository.findActiveUserById(deletedUser.getId());

        assertThat(result).isEmpty();
    }

    @Test
    void findActiveUserById_shouldReturnEmpty_whenUserDoesNotExist() {
        Optional<User> result = userRepository.findActiveUserById(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void findAllUserById_shouldReturnDeletedUser() {
        Optional<User> result = userRepository.findAllUserById(deletedUser.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(deletedUser.getId());
        assertThat(result.get().getIsActive()).isFalse();
        assertThat(result.get().getDeletedTime()).isNotNull();
    }

    @Test
    void findUserByIdIncludingDeleted_findAllUserById() {
        Optional<User> result = userRepository.findAllUserById(activeUser.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getIsActive()).isTrue();
    }

    @Test
    void existsBySsn_shouldReturnTrue_whenSsnExists() {
        boolean result = userRepository.existsBySsn("0000000000001111");

        assertThat(result).isTrue();
    }

    @Test
    void existsBySsn_shouldReturnFalse_whenSsnDoesNotExist() {
        boolean result = userRepository.existsBySsn("0000000000009999");

        assertThat(result).isFalse();
    }
}
