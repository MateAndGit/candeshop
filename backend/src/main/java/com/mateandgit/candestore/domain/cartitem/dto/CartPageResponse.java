package com.mateandgit.candestore.domain.cartitem.dto;

import org.springframework.data.domain.Page;

import java.math.BigDecimal;

public record CartPageResponse(
        Page<CartResponse> cartItem,
        BigDecimal totalCartPrice,
        Long totalCount
) {
}
