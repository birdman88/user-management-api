package com.springboottest.user_management_api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springboottest.user_management_api.util.validator.annotation.age.Age;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserRequest {

    @NotBlank(message = "First name is required")
    @Size(min = 3, max = 100, message = "First name must be between 3 and 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "First name cannot contain special characters")
    @JsonProperty("first_name")
    private String firstName;

    @Size(min = 3, max = 100, message = "Middle name must be between 3 and 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Middle name cannot contain special characters")
    @JsonProperty("middle_name")
    private String middleName;

    @NotBlank(message = "Last name is required")
    @Size(min = 3, max = 100, message = "Last name must be between 3 and 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Last name cannot contain special characters")
    @JsonProperty("last_name")
    private String lastName;

    @NotNull(message = "Birth date is required")
    @PastOrPresent(message = "Birth date cannot be in the future")
    @Age(max = 100, message = "Birth date cannot be older than 100 years")
    @JsonProperty("birth_date")
    private LocalDate birthDate;
}
