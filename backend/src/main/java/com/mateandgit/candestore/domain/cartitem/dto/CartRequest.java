package com.mateandgit.candestore.domain.cartitem.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CartRequest(

        @NotNull(message = "ID is required")
        Long productId,

        @Min(value = 1, message = "amount is required")
        int amount
) {
}
