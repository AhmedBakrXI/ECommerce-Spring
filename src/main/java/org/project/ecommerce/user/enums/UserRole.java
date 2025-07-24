package org.project.ecommerce.user.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRole {
    ADMIN("ADMIN"),
    CUSTOMER("CUSTOMER");

    private final String roleAsString;
}
