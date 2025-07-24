package org.project.ecommerce.product.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProductRequestDto {
    private String name;
    private String description;
    private double price;
    private int stockQuantity;

    @JsonProperty("isShippable")
    private boolean isShippable;

    @JsonProperty("isExpirable")
    private boolean isExpirable;
    private double weight;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime expirationDate;
}
