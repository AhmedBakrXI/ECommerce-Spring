package org.project.ecommerce.product.model;

import java.time.LocalDateTime;

public interface Expirable {
    LocalDateTime getExpiryDate();
    boolean isExpired();
}
