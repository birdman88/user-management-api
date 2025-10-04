package com.springboottest.user_management_api.controller;

import com.springboottest.user_management_api.dto.request.CreateUserRequest;
import com.springboottest.user_management_api.dto.request.UpdateUserRequest;
import com.springboottest.user_management_api.dto.request.UpdateUserSettingsRequest;
import com.springboottest.user_management_api.dto.response.UserListResponse;
import com.springboottest.user_management_api.dto.response.UserResponse;
import com.springboottest.user_management_api.service.interfaces.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    /**
     * GET /v1/users - Get paginated list of active users
     * */
    @GetMapping
    public ResponseEntity<UserListResponse> getAllUsers(
            @RequestParam(name = "max_records", defaultValue = "5") @Min(1) int maxRecords,
            @RequestParam(name = "offset", defaultValue = "0") @Min(0) int offset
    ) {

        log.info("GET /v1/users - maxRecords: {}, offset: {}", maxRecords, offset);
        UserListResponse response = userService.getAllUsers(maxRecords, offset);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /v1/users/{id} - Get user by ID with settings
     * */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        log.info("GET /v1/users/{}", id);
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /v1/users - Create new user
     * */
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        log.info("POST /v1/users - Creating user with SSN: {}", request.getSsn());
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * PUT /v1/users/{id} - Update user
     * */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        log.info("PUT /v1/users/{}", id);
        UserResponse response = userService.updateUser(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /v1/users/{id}/settings - Update user settings
     * */
    @PutMapping("/{id}/settings")
    public ResponseEntity<UserResponse> updateUserSettings(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserSettingsRequest request
    ) {
        log.info("PUT /v1/users/{}/settings", id);
        UserResponse response = userService.updateUserSettings(id, request.getSettings());
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /v1/users/{id} - Soft delete user
     * */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("DELETE /v1/users/{}", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * PUT /v1/users/{id}/refresh - Restore soft-deleted user
     * */
    @PutMapping("/{id}/refresh")
    public ResponseEntity<UserResponse> restoreUser(@PathVariable Long id) {
        log.info("PUT /v1/users/{}/refresh", id);
        UserResponse response = userService.restoreUser(id);
        return ResponseEntity.ok(response);
    }
}
