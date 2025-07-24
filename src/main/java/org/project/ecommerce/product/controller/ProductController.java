package org.project.ecommerce.product.controller;

import org.project.ecommerce.product.dto.ProductRequestDto;
import org.project.ecommerce.product.dto.ProductResponseDto;
import org.project.ecommerce.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ProductResponseDto> createProduct(@RequestPart("request") String requestAsJsonString,
            @RequestPart("image") MultipartFile image) {
        if (requestAsJsonString == null || image == null || image.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        ProductRequestDto request = productService.covertJsonToRequestDto(requestAsJsonString);
        if (request == null) {
            return ResponseEntity.badRequest().build();
        }
        ProductResponseDto response = productService.createProduct(request, image);
        if (response == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProduct(@PathVariable Long id) {
        ProductResponseDto productResponse = productService.getProductById(id);
        if (productResponse == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(productResponse);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
        List<ProductResponseDto> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/image/{path}")
    public ResponseEntity<Resource> getProductImage(@PathVariable String path) {
        Resource imageResource = productService.getImageResource(path);
        MediaType mediaType = productService.getMediaType(path);
        if (imageResource == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(imageResource);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        boolean isDeleted = productService.removeProduct(id);
        if (!isDeleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok("Item deleted successfully");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ProductResponseDto> updateProduct(@PathVariable Long id,
                                                            @RequestBody ProductRequestDto request) {
        ProductResponseDto response = productService.updateProduct(id, request);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }
}
