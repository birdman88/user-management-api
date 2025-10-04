package com.springboottest.user_management_api.service.interfaces;

import com.springboottest.user_management_api.dto.request.CreateUserRequest;
import com.springboottest.user_management_api.dto.request.UpdateUserRequest;
import com.springboottest.user_management_api.dto.response.UserListResponse;
import com.springboottest.user_management_api.dto.response.UserResponse;

import java.util.List;
import java.util.Map;

public interface UserService {

    /*
    * Get paginated list of active users
    * */
    UserListResponse getAllUsers(int maxRecords, int offset);

    /*
    * Get active user by id with settings
    * */
    UserResponse getUserById(Long id);

    /*
    * Create new user with default settings
    * */
    UserResponse createUser(CreateUserRequest request);

    /*
    * Update existing active user
    * */
    UserResponse updateUser(Long id, UpdateUserRequest request);

    /*
    * Soft delete user
    * */
    void deleteUser(Long id);

    /*
    * Restore soft delete user
    * */
    UserResponse restoreUser(Long id);

    /**
     * Update user settings
     */
    UserResponse updateUserSettings(Long id, List<Map<String, String>> settings);
}
