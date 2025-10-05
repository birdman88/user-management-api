package com.springboottest.user_management_api.service;

import com.springboottest.user_management_api.dto.request.CreateUserRequest;
import com.springboottest.user_management_api.dto.request.UpdateUserRequest;
import com.springboottest.user_management_api.dto.response.UserListResponse;
import com.springboottest.user_management_api.dto.response.UserResponse;
import com.springboottest.user_management_api.entity.User;
import com.springboottest.user_management_api.entity.UserSetting;
import com.springboottest.user_management_api.exception.DuplicateResourceException;
import com.springboottest.user_management_api.exception.InvalidRequestException;
import com.springboottest.user_management_api.exception.ResourceNotFoundException;
import com.springboottest.user_management_api.repository.UserRepository;
import com.springboottest.user_management_api.service.implementation.UserServiceImpl;
import com.springboottest.user_management_api.service.interfaces.UserSettingService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserSettingService userSettingService;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private CreateUserRequest createRequest;
    private UpdateUserRequest updateRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .ssn("0000000000002945")
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

        // Add sample setting
        UserSetting setting = UserSetting.builder()
                .id(1L)
                .key("biometric_login")
                .value("false")
                .user(user)
                .build();

        user.getUserSettings().add(setting);

        createRequest = CreateUserRequest.builder()
                .ssn("2945")
                .firstName("John")
                .lastName("Doe")
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        updateRequest = UpdateUserRequest.builder()
                .firstName("Jane")
                .lastName("Smith")
                .birthDate(LocalDate.of(1995, 5, 5))
                .build();
    }

    @Test
    void getAllUsers_shouldReturnPaginatedList() {
        Page<User> userPage = new PageImpl<>(List.of(user));
        when(userRepository.findAllActiveUsers(any(Pageable.class))).thenReturn(userPage);

        UserListResponse response = userService.getAllUsers(10, 0);

        assertThat(response.getUserData()).hasSize(1);
        assertThat(response.getMaxRecords()).isEqualTo(10);
        assertThat(response.getOffset()).isEqualTo(0);
        verify(userRepository).findAllActiveUsers(any(Pageable.class));
    }

    @Test
    void getUserById_shouldReturnUser_whenUserExists() {
        when(userRepository.findActiveUserById(1L)).thenReturn(Optional.of(user));
        UserResponse response = userService.getUserById(1L);

        assertThat(response.getUserData().getId()).isEqualTo(1L);
        assertThat(response.getUserSettings()).isNotEmpty();
        verify(userRepository).findActiveUserById(1L);
    }

    @Test
    void getUserById_shouldThrowException_whenUserNotFound() {
        when(userRepository.findActiveUserById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(userRepository).findActiveUserById(999L);
    }

    @Test
    void createUser_shouldCreateUserWithPaddedSsn() {
        when(userRepository.existsBySsn("0000000000002945")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userRepository.findActiveUserById(1L)).thenReturn(Optional.of(user));
        doNothing().when(entityManager).flush();
        doNothing().when(entityManager).clear();

        UserResponse response = userService.createUser(createRequest);

        assertThat(response.getUserData().getSsn()).isEqualTo("0000000000002945");
        verify(userRepository).existsBySsn("0000000000002945");
        verify(userRepository).save(any(User.class));
        verify(userSettingService).createDefaultSettings(any(User.class));
    }

    @Test
    void createUser_shouldThrowException_whenSsnAlreadyExists() {
        when(userRepository.existsBySsn("0000000000002945")).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(createRequest))
                .isInstanceOf(DuplicateResourceException.class);
        verify(userRepository).existsBySsn("0000000000002945");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_shouldThrowException_whenAgeIsOlderThan100Years() {
        createRequest.setBirthDate(LocalDate.now().minusYears(101));

        assertThatThrownBy(() -> userService.createUser(createRequest))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("Birth date cannot be older than 100 years");
    }

    @Test
    void updateUser_shouldUpdateUser_whenUserExists() {
        when(userRepository.findActiveUserById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponse response = userService.updateUser(1L, updateRequest);

        assertThat(response.getUserData().getFirstName()).isEqualTo("Jane");
        verify(userRepository, times(2)).findActiveUserById(1L); // fetch twice
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_shouldThrowException_whenUserNotFound() {
        when(userRepository.findActiveUserById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(999L, updateRequest))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateUser_shouldThrowException_whenAgeIsOlderThan100Years() {
        when(userRepository.findActiveUserById(1L)).thenReturn(Optional.of(user));
        updateRequest.setBirthDate(LocalDate.now().minusYears(101));

        assertThatThrownBy(() -> userService.updateUser(1L, updateRequest))
                .isInstanceOf(InvalidRequestException.class);
    }

    @Test
    void deleteUser_shouldSoftDeleteUser() {
        when(userRepository.findActiveUserById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.deleteUser(1L);

        verify(userRepository).findActiveUserById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void deleteUser_shouldThrowException_whenUserNotFound() {
        when(userRepository.findActiveUserById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUser(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void restoreUser_shouldRestoreDeletedUser() {
        user.setIsActive(false);
        user.setDeletedTime(Instant.now());
        when(userRepository.findAllUserById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponse response = userService.restoreUser(1L);

        assertThat(response.getUserData().getIsActive()).isTrue();
        verify(userRepository).findAllUserById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void restoreUser_shouldThrowException_whenUserNotFound() {
        when(userRepository.findAllUserById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.restoreUser(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void restoreUser_shouldThrowException_whenUserIsAlreadyActive() {
        user.setIsActive(true);
        user.setDeletedTime(null);
        when(userRepository.findAllUserById(1L)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.restoreUser(1L))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("User is already active");
    }
}
