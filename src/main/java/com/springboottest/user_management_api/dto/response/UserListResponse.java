package com.springboottest.user_management_api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserListResponse {

    @JsonProperty("user_data")
    private List<UserResponse.UserData> userData;

    @JsonProperty("max_records")
    private Integer maxRecords;

    private Integer offset;
}
