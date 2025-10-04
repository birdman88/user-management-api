package com.springboottest.user_management_api.service.implementation;

import com.springboottest.user_management_api.dto.request.CreateUserRequest;
import com.springboottest.user_management_api.dto.request.UpdateUserRequest;
import com.springboottest.user_management_api.dto.response.UserListResponse;
import com.springboottest.user_management_api.dto.response.UserResponse;
import com.springboottest.user_management_api.entity.User;
import com.springboottest.user_management_api.exception.DuplicateResourceException;
import com.springboottest.user_management_api.exception.InvalidRequestException;
import com.springboottest.user_management_api.exception.ResourceNotFoundException;
import com.springboottest.user_management_api.repository.UserRepository;
import com.springboottest.user_management_api.service.interfaces.UserService;
import com.springboottest.user_management_api.service.interfaces.UserSettingService;
import com.springboottest.user_management_api.util.ResponseUtil;
import com.springboottest.user_management_api.util.SsnUtil;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserSettingService userSettingService;
    private final EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public UserListResponse getAllUsers(int maxRecords, int offset) {
        log.info("Fetching users with maxRecords: {}, offset: {}", maxRecords, offset);

        Pageable pageable = PageRequest.of(offset, maxRecords);
        Page<User> userPage = userRepository.findAllActiveUsers(pageable);

        List<UserResponse.UserData> userDataList = userPage.getContent().stream()
                .map(ResponseUtil::mapToUserData)
                .collect(Collectors.toList());

        return UserListResponse.builder()
                .userData(userDataList)
                .maxRecords(maxRecords)
                .offset(offset)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        log.info("Fetching user by id: {}", id);

        User user = userRepository.findActiveUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));
        return ResponseUtil.mapToUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        log.info("Creating new user with SSN: {}", request.getSsn());

        // pad ssn with leading zeros
        String paddedSsn = SsnUtil.padSSN(request.getSsn());

        //check if ssn already exist
        if (userRepository.existsBySsn(paddedSsn)) {
            log.error("SSN already exists: {}", paddedSsn);
            throw new DuplicateResourceException(paddedSsn);
        }

        //validate birthdate
        validateBirthDate(request.getBirthDate());

        //create user entity
        User user = User.builder()
                .ssn(paddedSsn)
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .familyName(request.getLastName())
                .birthDate(request.getBirthDate())
                .isActive(true)
                .build();

        //save new user
        User savedUser = userRepository.save(user);
        log.info("User created successfully with id: {}", savedUser.getId());

        //create default settings for user
        userSettingService.createDefaultSettings(savedUser);

        // Clear persistence context to force fresh fetch, flush all changes user and user settings
        entityManager.flush();
        entityManager.clear();

        //fetch user with settings
        User userWithSettings = userRepository.findActiveUserById(savedUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException(savedUser.getId()));

        return ResponseUtil.mapToUserResponse(userWithSettings);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        log.info("Updating user with id: {}", id);

        // find active user
        User user = userRepository.findActiveUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));

        // validate birthdate
        validateBirthDate(request.getBirthDate());

        //update only allowed fields
        user.setFirstName(request.getFirstName());
        user.setMiddleName(request.getMiddleName());
        user.setFamilyName(request.getLastName());
        user.setBirthDate(request.getBirthDate());

        //save updated user
        User updatedUser = userRepository.save(user);
        log.info("User updated successfully with id: {}", updatedUser.getId());

        //fetch user with settings
        User userWithSettings = userRepository.findActiveUserById(updatedUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException(updatedUser.getId()));


        return ResponseUtil.mapToUserResponse(userWithSettings);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        log.info("Soft deleting user with id: {}", id);

        //find active user
        User user = userRepository.findActiveUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));

        // soft delete
        user.setIsActive(false);
        user.setDeletedTime(Instant.now());

        userRepository.save(user);
        log.info("User soft deleted successfully with id: {}", id);
    }

    @Override
    @Transactional
    public UserResponse restoreUser(Long id) {
        log.info("Restoring user with id: {}", id);

        // find user including deleted ones
        User user = userRepository.findAllUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));

        // check if user is actually deleted
        if (user.getIsActive() && user.getDeletedTime() == null) {
            log.warn("User with id {} is already active", id);
            throw new InvalidRequestException("User is already active");
        }

        //restore user
        user.setIsActive(true);
        user.setDeletedTime(null);

        User restoredUser = userRepository.save(user);
        log.info("User restored successfully with id: {}", id);

        return ResponseUtil.mapToUserResponse(restoredUser);
    }

    @Override
    @Transactional
    public UserResponse updateUserSettings(Long id, List<Map<String, String>> settings) {
        log.info("Updating settings for user with id: {}", id);
        return userSettingService.updateUserSettings(id, settings);
    }

    /*
    * validate birthdate cannot be older than 100 years
    * */
    private void validateBirthDate(LocalDate birthDate) {
        LocalDate hundredYrsAgo = LocalDate.now().minusYears(100);
        if (birthDate.isBefore(hundredYrsAgo)) {
            log.error("Birth date is older than 100 years: {}",
                    birthDate);

            throw new InvalidRequestException(
                    String.format("Birth date cannot be older than " +
                            "100 years, rejected value: %s", birthDate)
            );
        }
    }

//    /**
//     * Map User entity to UserData DTO
//     */
//    private UserResponse.UserData mapToUserData(User user) {
//        return UserResponse.UserData.builder()
//                .id(user.getId())
//                .ssn(user.getSsn())
//                .firstName(user.getFirstName())
//                .middleName(user.getMiddleName())
//                .familyName(user.getFamilyName())
//                .birthDate(user.getBirthDate())
//                .createdTime(user.getCreatedTime())
//                .updatedTime(user.getUpdatedTime())
//                .createdBy(user.getCreatedBy())
//                .updatedBy(user.getUpdatedBy())
//                .isActive(user.getIsActive())
//                .deletedTime(user.getDeletedTime())
//                .build();
//    }
//
//    /**
//     * Map User entity to UserResponse with settings DTO
//     */
//    private UserResponse mapToUserResponse(User user) {
//        UserResponse.UserData userData = mapToUserData(user);
//
//        // Map settings to list of maps format
//        List<Map<String, String>> settingList = user.getUserSettings()
//                .stream()
//                .map(setting -> Map.of(setting.getKey(), setting.getValue()))
//                .collect(Collectors.toList());
//
//        return UserResponse.builder()
//                .userData(userData)
//                .userSettings(settingList)
//                .build();
//    }
}
