package org.project.ecommerce.user.dto.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRequest {
    @NotNull
    @NotBlank(message = "username cannot be blank")
    @Size(
            min = 5,
            max = 50
    )
    @Pattern(
            regexp = "^\\p{Nd}"
    )
    @Schema(
            name = "Username",
            example = "Ahmed",
            minimum = "5 Letters and numbers",
            maximum = "50 Letters and numbers"
    )
    private String username;

    @NotNull
    private String password;

    @Email
    @NotNull
    private String email;
}
