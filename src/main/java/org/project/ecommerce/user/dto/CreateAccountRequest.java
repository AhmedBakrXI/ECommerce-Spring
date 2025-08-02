package org.project.ecommerce.user.dto;

import lombok.Data;

@Data
public class CreateAccountRequest {
    private String username;
    private String email;
    private String password;
    private String confirmPassword;
}
