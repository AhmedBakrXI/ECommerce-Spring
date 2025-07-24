package org.project.ecommerce.product.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.project.ecommerce.product.dto.ProductRequestDto;
import org.project.ecommerce.product.dto.ProductResponseDto;
import org.project.ecommerce.product.model.*;
import org.project.ecommerce.product.repository.ProductRepository;
import org.project.ecommerce.product.service.files.ImageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    private ImageHandler imageHandler;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @CachePut(value = "productCache", key = "#result.id")
    public ProductResponseDto createProduct(ProductRequestDto request, MultipartFile imageFile) {
        if (request == null) {
            return null;
        }

        Product product;
        if (request.isExpirable()) {
            if (request.isShippable()) {
                product = newShippableExpirableProduct(request);
            } else {
                product = newNonShippableExpirableProduct(request);
            }
        } else {
            if (request.isShippable()) {
                product = newShippableNonExpirableProduct(request);
            } else {
                product = newNonShippableNonExpirableProduct(request);
            }
        }
        try {
            String imagePath = imageHandler.saveImage(request.getName(), imageFile);
            product.setImageName(imagePath);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null; // Handle image saving failure
        }
        product = productRepository.save(product);
        return convertToResponseDto(product, request.isShippable(), request.isExpirable());
    }

    private Product newNonShippableNonExpirableProduct(ProductRequestDto request) {
        return NonExpirableProduct.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .build();
    }

    private Product newShippableNonExpirableProduct(ProductRequestDto request) {
        return ShippableNonExpirableProduct.builder()
                .name(request.getName())
                .description(request.getDescription())
                .stockQuantity(request.getStockQuantity())
                .price(request.getPrice())
                .weight(request.getWeight())
                .build();
    }

    private Product newNonShippableExpirableProduct(ProductRequestDto request) {
        return NonExpirableProduct.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .build();
    }


    private Product newShippableExpirableProduct(ProductRequestDto request) {
        return ShippableExpirableProduct.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .weight(request.getWeight())
                .expiryDate(request.getExpirationDate())
                .build();
    }

    private ProductResponseDto convertToResponseDto(Product product, boolean isShippable, boolean isExpirable) {
        if (product == null) {
            return null;
        }

        return ProductResponseDto.builder()
                .id(product.getId().intValue())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .isShippable(isShippable)
                .isExpirable(isExpirable)
                .imageUrl(product.getImageName())
                .build();
    }

    public ProductRequestDto covertJsonToRequestDto(String json) {
        try {
            return objectMapper.readValue(json, ProductRequestDto.class);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null; // Handle JSON parsing failure
        }
    }

    public Resource getImageResource(String path) {
        return imageHandler.getImageResource(path);
    }

    public MediaType getMediaType(String path) {
        String extension = path.substring(path.lastIndexOf('.') + 1).toLowerCase();
        switch (extension) {
            case "png" -> {
                return MediaType.IMAGE_PNG;
            }
            case "jpg", "jpeg" -> {
                return MediaType.IMAGE_JPEG;
            }
            default -> {
                return MediaType.APPLICATION_OCTET_STREAM; // Default to binary stream if unknown type
            }
        }
    }

    @CacheEvict(value = "productCache", key = "#id")
    public boolean removeProduct(Long id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isEmpty()) {
            return false; // Product not found
        }
        String imagePath = imageHandler.getImageUrl(product.get().getImageName());
        if (imagePath == null || imagePath.isEmpty()) {
            return false;
        }
        if (!imageHandler.deleteImage(imagePath)) {
            return false;
        }
        productRepository.deleteById(id);
        return true;
    }

    @CachePut(value = "productCache", key = "#id")
    public ProductResponseDto updateProduct(Long id, ProductRequestDto request) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            product.setName(request.getName());
            product.setDescription(request.getDescription());
            product.setPrice(request.getPrice());
            product.setStockQuantity(request.getStockQuantity());

            if (request instanceof Shippable) {
                if (product instanceof ShippableNonExpirableProduct) {
                    ((ShippableNonExpirableProduct) product).setWeight(request.getWeight());
                } else if (product instanceof ShippableExpirableProduct) {
                    ((ShippableExpirableProduct) product).setWeight(request.getWeight());
                    ((ShippableExpirableProduct) product).setExpiryDate(request.getExpirationDate());
                }
            }

            Product updatedProduct = productRepository.save(product);

            return convertToResponseDto(updatedProduct, request.isShippable(), request.isExpirable());
        }
        return null;
    }

    public List<ProductResponseDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(
                        product -> convertToResponseDto(
                                product, product instanceof Shippable, product instanceof Expirable
                        )
                )
                .toList();
    }

    @Cacheable(value = "productCache", key = "#id")
    public ProductResponseDto getProductById(Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            return convertToResponseDto(
                    product, product instanceof Shippable, product instanceof Expirable
            );
        }
        return null; // Product not found
    }
}
