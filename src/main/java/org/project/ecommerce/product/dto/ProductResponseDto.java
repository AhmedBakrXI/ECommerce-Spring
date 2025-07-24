package org.project.ecommerce.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDto {
    private int id;
    private String name;
    private String description;
    private String imageUrl;
    private double price;
    private int stockQuantity;
    private boolean isShippable;
    private boolean isExpirable;
}
