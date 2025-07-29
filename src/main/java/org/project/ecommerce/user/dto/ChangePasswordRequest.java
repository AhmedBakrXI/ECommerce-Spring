package org.project.ecommerce.user.dto;

import lombok.Data;

@Data
public class ChangePasswordRequest {
    private String username;
    private String currentPassword;
    private String confirmPassword;
    private String newPassword;
}
