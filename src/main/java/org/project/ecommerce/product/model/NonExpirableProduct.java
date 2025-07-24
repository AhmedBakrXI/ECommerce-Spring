package org.project.ecommerce.product.model;

import lombok.*;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class NonExpirableProduct extends Product {
}
