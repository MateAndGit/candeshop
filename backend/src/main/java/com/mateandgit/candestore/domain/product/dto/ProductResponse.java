package com.mateandgit.candestore.domain.product.dto;

import com.mateandgit.candestore.domain.product.entity.Product;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String title,
        String description,
        BigDecimal price
) {
    public ProductResponse(Product product) {
        this(
                product.getId(),
                product.getTitle(),
                product.getDescription(),
                product.getPrice()
        );
    }
}
