package org.project.ecommerce.user.exceptions;

public class UsernameExistsException extends UserException {
    public UsernameExistsException(String message) {
        super(message);
    }
}
