package org.project.ecommerce.product.model;

import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ExpirableProduct extends Product implements Expirable {
    public LocalDateTime expiryDate;

    @Override
    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    @Override
    public boolean isExpired() {
        return getExpiryDate().isBefore(LocalDateTime.now());
    }
}
