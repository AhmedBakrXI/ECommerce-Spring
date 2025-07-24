package org.project.ecommerce.product.model;

import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ShippableNonExpirableProduct extends NonExpirableProduct implements Shippable {
    double weight;

    @Override
    public double getWeight() {
        return weight;
    }
}

