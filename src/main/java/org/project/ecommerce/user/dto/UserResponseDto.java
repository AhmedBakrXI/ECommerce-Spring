package org.project.ecommerce.user.dto;

import lombok.Builder;
import lombok.Data;
import org.project.ecommerce.user.enums.UserResponseCode;
import org.project.ecommerce.user.enums.UserRole;

@Builder
@Data
public class UserResponseDto {
    private String username;
    private UserResponseCode userResponseCode;
}
