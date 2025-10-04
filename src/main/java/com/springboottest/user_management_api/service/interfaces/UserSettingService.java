package com.springboottest.user_management_api.service.interfaces;

import com.springboottest.user_management_api.dto.response.UserResponse;
import com.springboottest.user_management_api.entity.User;

import java.util.List;
import java.util.Map;

public interface UserSettingService {

    /*
    * Create default user settings for a new user
    * */
    void createDefaultSettings(User user);

    /*
    * update user settings
    * */
    UserResponse updateUserSettings(Long userId, List<Map<String, String>> settings);
}
