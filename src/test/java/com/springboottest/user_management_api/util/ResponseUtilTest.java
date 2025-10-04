package com.springboottest.user_management_api.util;

import com.springboottest.user_management_api.dto.response.UserResponse;
import com.springboottest.user_management_api.entity.User;
import com.springboottest.user_management_api.entity.UserSetting;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ResponseUtilTest {

    @Test
    void MapToUserResponse_Success() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setSsn("0000000000000001");
        user.setFirstName("John");
        user.setMiddleName("M");
        user.setFamilyName("Doe");
        user.setBirthDate(LocalDate.of(1990, 5, 20));
        user.setCreatedTime(Instant.now().minusSeconds(2 * 24 * 60 * 60));
        user.setUpdatedTime(Instant.now());
        user.setCreatedBy("admin");
        user.setUpdatedBy("admin2");
        user.setIsActive(true);

        // Mock settings
        UserSetting setting1 = new UserSetting();
        setting1.setKey("biometric_login");
        setting1.setValue("false");

        UserSetting setting2 = new UserSetting();
        setting2.setKey("widget_order");
        setting2.setValue("1,2,3,4,5");

        user.setUserSettings(List.of(setting1, setting2));

        // Act
        UserResponse response = ResponseUtil.mapToUserResponse(user);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getUserData());
        assertEquals(user.getId(), response.getUserData().getId());
        assertEquals(user.getSsn(), response.getUserData().getSsn());
        assertEquals(user.getFirstName(), response.getUserData().getFirstName());
        assertEquals(2, response.getUserSettings().size());

        // Verify map content
        Map<String, String> firstSetting = response.getUserSettings().get(0);
        assertTrue(firstSetting.containsKey("biometric_login"));
        assertEquals("false", firstSetting.get("biometric_login"));

        Map<String, String> secondSetting = response.getUserSettings().get(1);
        assertTrue(secondSetting.containsKey("widget_order"));
        assertEquals("1,2,3,4,5", secondSetting.get("widget_order"));
    }

    @Test
    void MapToUserResponse_EmptySettings() {
        // Arrange
        User user = new User();
        user.setId(2L);
        user.setUserSettings(List.of());

        // Act
        UserResponse response = ResponseUtil.mapToUserResponse(user);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getUserSettings());
        assertTrue(response.getUserSettings().isEmpty());
    }
}
