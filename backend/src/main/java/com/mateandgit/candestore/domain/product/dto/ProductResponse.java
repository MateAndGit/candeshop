package com.mateandgit.candestore.domain.product.dto;

import com.mateandgit.candestore.domain.product.entity.Product;

public record ProductResponse(
        Long id,
        String title,
        String description,
        int price
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
