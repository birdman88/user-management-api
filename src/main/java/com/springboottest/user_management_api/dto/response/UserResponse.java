package com.springboottest.user_management_api.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {

    @JsonProperty("user_data")
    private UserData userData;

    @JsonProperty("user_settings")
    private List<Map<String, String>> userSettings;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class UserData {
        private Long id;
        private String ssn;

        @JsonProperty("first_name")
        private String firstName;

        @JsonProperty("middle_name")
        private String middleName;

        @JsonProperty("last_name")
        private String familyName;

        @JsonProperty("birth_date")
        private LocalDate birthDate;

        @JsonProperty("created_time")
        private Instant createdTime;

        @JsonProperty("updated_time")
        private Instant updatedTime;

        @JsonProperty("created_by")
        private String createdBy;

        @JsonProperty("updated_by")
        private String updatedBy;

        @JsonProperty("is_active")
        private Boolean isActive;

        @JsonProperty("deleted_time")
        private Instant deletedTime;
    }
}
