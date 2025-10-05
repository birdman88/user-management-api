package com.springboottest.user_management_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboottest.user_management_api.dto.request.CreateUserRequest;
import com.springboottest.user_management_api.dto.request.UpdateUserRequest;
import com.springboottest.user_management_api.dto.request.UpdateUserSettingsRequest;
import com.springboottest.user_management_api.dto.response.UserListResponse;
import com.springboottest.user_management_api.dto.response.UserResponse;
import com.springboottest.user_management_api.exception.DuplicateResourceException;
import com.springboottest.user_management_api.exception.ResourceNotFoundException;
import com.springboottest.user_management_api.service.interfaces.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    private UserResponse userResponse;
    private CreateUserRequest createRequest;
    private UpdateUserRequest updateRequest;

    @BeforeEach
    void setUp() {
        UserResponse.UserData userData = UserResponse.UserData.builder()
                .id(1L)
                .ssn("0000000000002945")
                .firstName("John")
                .familyName("Doe")
                .birthDate(LocalDate.of(1990, 1, 1))
                .createdTime(Instant.now())
                .updatedTime(Instant.now())
                .createdBy("SYSTEM")
                .updatedBy("SYSTEM")
                .isActive(true)
                .build();

        List<Map<String, String>> settings = List.of(
                Map.of("biometric_login", "false"),
                Map.of("push_notification", "false")
        );

        userResponse = UserResponse.builder()
                .userData(userData)
                .userSettings(settings)
                .build();

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
    void getAllUsers_shouldReturn200() throws Exception {
        UserListResponse listResponse = UserListResponse.builder()
                .userData(List.of(userResponse.getUserData()))
                .maxRecords(10)
                .offset(0)
                .build();
        when(userService.getAllUsers(10, 0)).thenReturn(listResponse);

        mockMvc.perform(get("/v1/users")
                        .param("max_records", "10")
                        .param("offset", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_data").isArray())
                .andExpect(jsonPath("$.max_records").value(10));

        verify(userService).getAllUsers(10, 0);
    }

    @Test
    void getUserById_shouldReturn200_whenUserExists() throws Exception {
        when(userService.getUserById(1L)).thenReturn(userResponse);

        mockMvc.perform(get("/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_data.id").value(1))
                .andExpect(jsonPath("$.user_settings").isArray());

        verify(userService).getUserById(1L);
    }

    @Test
    void getUserById_shouldReturn404_whenUserNotFound() throws Exception {
        when(userService.getUserById(999L))
                .thenThrow(new ResourceNotFoundException(999L));

        mockMvc.perform(get("/v1/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(30000));
    }

    @Test
    void createUser_shouldReturn201() throws Exception {
        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(userResponse);

        mockMvc.perform(post("/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user_data.id").value(1));

        verify(userService).createUser(any(CreateUserRequest.class));
    }

    @Test
    void createUser_shouldReturn422_whenValidationFails() throws Exception {
        createRequest.setFirstName("");

        mockMvc.perform(post("/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value(30002));
    }

    @Test
    void createUser_shouldReturn409_whenSsnAlreadyExists() throws Exception {
        when(userService.createUser(any(CreateUserRequest.class)))
                .thenThrow(new DuplicateResourceException("0000000000002945"));

        mockMvc.perform(post("/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(30001));
    }

    @Test
    void updateUser_shouldReturn200() throws Exception {
        when(userService.updateUser(eq(1L), any(UpdateUserRequest.class)))
                .thenReturn(userResponse);

        mockMvc.perform(put("/v1/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_data.id").value(1));

        verify(userService).updateUser(eq(1L), any(UpdateUserRequest.class));
    }

    @Test
    void updateUser_shouldReturn404_whenUserNotFound() throws Exception {
        when(userService.updateUser(eq(999L), any(UpdateUserRequest.class)))
                .thenThrow(new ResourceNotFoundException(999L));

        mockMvc.perform(put("/v1/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUserSettings_shouldReturn200() throws Exception {
        UpdateUserSettingsRequest settingsRequest = UpdateUserSettingsRequest.builder()
                .settings(List.of(Map.of("biometric_login", "true")))
                .build();
        when(userService.updateUserSettings(eq(1L), anyList()))
                .thenReturn(userResponse);

        mockMvc.perform(put("/v1/users/1/settings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(settingsRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_data.id").value(1));

        verify(userService).updateUserSettings(eq(1L), anyList());
    }

    @Test
    void deleteUser_shouldReturn204() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/v1/users/1"))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(1L);
    }

    @Test
    void deleteUser_shouldReturn404_whenUserNotFound() throws Exception {
        doThrow(new ResourceNotFoundException(999L))
                .when(userService).deleteUser(999L);

        mockMvc.perform(delete("/v1/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void restoreUser_shouldReturn200() throws Exception {
        when(userService.restoreUser(1L)).thenReturn(userResponse);

        mockMvc.perform(put("/v1/users/1/refresh"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_data.id").value(1));

        verify(userService).restoreUser(1L);
    }

}
