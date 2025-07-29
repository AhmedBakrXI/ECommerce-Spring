package org.project.ecommerce.user.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserResponseCode {
    SUCCESS(0, "Success"),
    USER_NOT_FOUND(1, "User Not Found"),
    INCORRECT_PASSWORD(2, "Incorrect Password"),
    USER_NOT_AUTHORIZED(3, "Not Authorized"),
    USER_ALREADY_EXISTS(4, "User Already Exists"),
    ;

    private final int code;
    private final String message;
}
