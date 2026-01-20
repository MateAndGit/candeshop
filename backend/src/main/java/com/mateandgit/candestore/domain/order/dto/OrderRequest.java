package com.mateandgit.candestore.domain.order.dto;

import java.math.BigDecimal;
import java.util.List;

public record OrderRequest(
        List<OrderItemDto> orderItems,
        BigDecimal totalPrice,
        Long totalCount
) {
    public record OrderItemDto(
            Long productId,
            BigDecimal price,
            Long count
    ) {}
}
