package com.api_application.phase_2_data_guardian.infra.http.mapper;

import com.api_application.phase_2_data_guardian.domain.entity.User;
import com.api_application.phase_2_data_guardian.infra.http.dto.response.UserResponse;
import com.api_application.phase_2_data_guardian.infra.http.dto.response.UserSummaryResponse;
import org.springframework.stereotype.Component;

@Component
public class UserResponseMapper {
    public UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(),
                user.getUserTypeId(), user.getCreatedAt(), user.getUpdatedAt());
    }

    public UserSummaryResponse toSummary(User user) {
        return new UserSummaryResponse(user.getId(), user.getName(), user.getUserTypeId());
    }
}
