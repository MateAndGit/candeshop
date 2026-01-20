package com.mateandgit.candestore.domain.cartitem.dto;

import com.mateandgit.candestore.domain.cartitem.entity.CartItem;

import java.math.BigDecimal;

public record CartResponse (
        Long cartItemId,
        Long productId,
        String title,
        BigDecimal price,
        Long amount
){
    public CartResponse(CartItem cartItem) {
        this(
                cartItem.getId(),
                cartItem.getProduct().getId(),
                cartItem.getProduct().getTitle(),
                cartItem.getProduct().getPrice(),
                cartItem.getAmount()
        );
    }
}
