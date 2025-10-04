package com.springboottest.user_management_api.util;

import com.springboottest.user_management_api.dto.response.UserResponse;
import com.springboottest.user_management_api.entity.User;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class ResponseUtil {

    /*
    * Helper method to map User entity to UserData DTO
    * */
    public static UserResponse.UserData mapToUserData(User user) {
        return UserResponse.UserData.builder()
                .id(user.getId())
                .ssn(user.getSsn())
                .firstName(user.getFirstName())
                .middleName(user.getMiddleName())
                .familyName(user.getFamilyName())
                .birthDate(user.getBirthDate())
                .createdTime(user.getCreatedTime())
                .updatedTime(user.getUpdatedTime())
                .createdBy(user.getCreatedBy())
                .updatedBy(user.getUpdatedBy())
                .isActive(user.getIsActive())
                .deletedTime(user.getDeletedTime())
                .build();
    }

    /*
     * Map User entity to UserResponse with settings DTO
     * */
    public static UserResponse mapToUserResponse(User user) {
        UserResponse.UserData userData = mapToUserData(user);

        // Map settings to list of maps format
        log.info("check inside {}", user.getUserSettings());
        List<Map<String, String>> settingList = user.getUserSettings()
                .stream()
                .map(setting -> Map.of(setting.getKey(), setting.getValue()))
                .collect(Collectors.toList());

        return UserResponse.builder()
                .userData(userData)
                .userSettings(settingList)
                .build();
    }
}
